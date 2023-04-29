package com.ljh.project.controller;

import com.ljh.ljhclientsdk.client.LjhClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("/test")
public class test {

    @Resource
    private LjhClient ljhClient;

    @GetMapping("/test1")
    public void test(){

        //调用可用接口
        String result = ljhClient.getKnow();


    }


}
