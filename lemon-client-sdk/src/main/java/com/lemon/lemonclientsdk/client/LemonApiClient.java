package com.lemon.lemonclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.lemon.lemonclientsdk.model.User;


import java.util.HashMap;
import java.util.Map;

import static com.lemon.lemonclientsdk.utils.SignUtils.getSign;


/**
 * 调用第三方接口的客户端
 */
public class LemonApiClient {

    private String accessKey;
    private String secretKey;

    public static final String GATEWAY_HOST = "http://localhost:8090";
    public LemonApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String username){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name",username);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String username){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name",username);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getUserByPost(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        return result;
    }

    private Map<String,String> getHeaderMap(String body){
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("accessKey",accessKey);
        //headerMap.put("secretKey",secretKey);
        headerMap.put("nonce", RandomUtil.randomNumbers(4));
        headerMap.put("body",body);
        //当前时间戳
        headerMap.put("timeStamp",String.valueOf(System.currentTimeMillis()/1000));
        headerMap.put("sign",getSign(body,secretKey));
        return headerMap;
    }
}
