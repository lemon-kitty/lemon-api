package com.lemon.lemonapiinterface;

import com.lemon.lemonclientsdk.client.LemonApiClient;
import com.lemon.lemonclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class LemonApiInterfaceApplicationTests {

    @Resource
    private LemonApiClient lemonApiClient;
    @Test
    void contextLoads() {
        String result = lemonApiClient.getNameByGet("lemon");
        User user = new User();
        user.setUsername("cat");
        String userByPost = lemonApiClient.getUserByPost(user);
        System.out.println(result);
        System.out.println(userByPost);
    }

}
