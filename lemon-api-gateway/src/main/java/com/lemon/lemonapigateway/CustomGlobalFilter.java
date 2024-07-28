package com.lemon.lemonapigateway;

import com.lemon.lemonapicommon.model.entity.InterfaceInfo;
import com.lemon.lemonapicommon.model.entity.User;
import com.lemon.lemonapicommon.service.InnerInterfaceInfoService;
import com.lemon.lemonapicommon.service.InnerUserInterfaceInfoService;
import com.lemon.lemonapicommon.service.InnerUserService;
import com.lemon.lemonclientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8123";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1. 用户发送请求到API网关
        //2. 请求日志
        //拿到请求对象
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求方法：" + method);
        log.info("请求路径：" + path);
        log.info("请求参数：" + request.getQueryParams());
        log.info("请求来源地址：" + request.getRemoteAddress());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);

        //拿到响应对象
        ServerHttpResponse response = exchange.getResponse();

        //3. （黑白名单）
        if (!IP_WHITE_LIST.contains(sourceAddress)){
            return handleNoAuth(response);
        }

        //4. 用户鉴权（判断ak,sk是否合法）
        //从请求参数中获取参数并校验
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String sign = headers.getFirst("sign");
        String timeStamp = headers.getFirst("timeStamp");
        String body = headers.getFirst("body");

        //从数据库中查询该用户是否有accessKey
        User invokeUser = null;
        try {
            //调用内部服务
            invokeUser = innerUserService.getInvokeUser(accessKey);
        }catch (Exception e){
            log.error("getInvokeUser error",e);
        }
        if (invokeUser == null){
            //如果用户信息为空，处理未授权情况并返回响应
            return handleNoAuth(response);
        }

        if (Long.parseLong(nonce) > 10000){
            return handleNoAuth(response);
        }
        //时间和当前时间不能超过5分钟
        Long currentSeconds = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 5 * 60L;
        if ((currentSeconds - Long.parseLong(timeStamp)) >= FIVE_MINUTES){
            return handleNoAuth(response);
        }

        //实际是从数据库中获取密钥
        //String severSign = SignUtils.getSign(body, "abcdefgh");
        String secretKey = invokeUser.getSecretKey();
        String severSign = SignUtils.getSign(body, secretKey);
        if (sign == null || !sign.equals(severSign)){
            return handleInvokeError(response);
        }

        //5. 请求的模拟接口是否存在
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path,method);
        }catch (Exception e){
            log.error("getInterfaceInfo error",e);
        }
        if (interfaceInfo == null){
            return handleNoAuth(response);
        }
        /*
        //6. 请求转发，调用模拟接口
        Mono<Void> filter = chain.filter(exchange);
        //7. 响应日志
        log.info("响应：" + response.getStatusCode());

        //8. 调用成功，接口调用次数+1
        if (response.getStatusCode() == HttpStatus.OK){

        }else{
            //9. 调用失败，返回一个规范的错误码
            return handleInvokeError(response);
        }

        log.info("custom global filter");
        return filter;*/

        return handleResponse(exchange,chain,interfaceInfo.getId(),invokeUser.getId());
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    /**
     * 处理响应
     * @param exchange
     * @param chain
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            //获取原始的响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    //处理响应体的数据
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }
}

