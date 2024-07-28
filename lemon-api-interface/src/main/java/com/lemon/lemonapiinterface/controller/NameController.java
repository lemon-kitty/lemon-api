package com.lemon.lemonapiinterface.controller;

import com.lemon.lemonclientsdk.model.User;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


import static com.lemon.lemonclientsdk.utils.SignUtils.getSign;

@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name){
        return "Get 你的名字是："+name;
    }
    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name){
        return "Post 你的名字是：" + name;
    }

    @PostMapping("/user")
    public String getUserByPost(@RequestBody User user, HttpServletRequest request){
        //从请求参数中获取参数并校验
        /*String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String sign = request.getHeader("sign");
        String timeStamp = request.getHeader("timeStamp");
        String body = request.getHeader("body");
        //从数据库中查询该用户是否有accessKey

        if (Long.parseLong(nonce) > 10000){
            throw new RuntimeException("无权限");
        }

        //时间和当前时间不能超过5分钟

        //String secretKey = request.getHeader("secretKey");
        if (!accessKey.equals("lemon") && !secretKey.equals("abcdefg")){
            throw new RuntimeException("无权限");
        }
        
        //实际是从数据库中获取密钥
        String severSign = getSign(body, "abcdefgh");
        if (!sign.equals(severSign)){
            throw new RuntimeException("无权限");
        }*/

        return "Post 用户的名字是；" + user.getUsername();
    }
}
