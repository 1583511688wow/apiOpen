package com.ljh.example.testsdk;

import com.ljh.ljhclientsdk.client.LjhClient;
import com.ljh.ljhclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TestsdkApplicationTests {

    @Resource
    private LjhClient ljhClient;

    @Test
    void contextLoads() {

        ljhClient.getNameByGet("fsf");
        ljhClient.getNameByPost("666");

        User dad = new User();
        dad.setName("dads");
        ljhClient.getUsernameByPost(dad);


    }

}
