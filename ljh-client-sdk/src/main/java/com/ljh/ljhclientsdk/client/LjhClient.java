package com.ljh.ljhclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ljh.ljhclientsdk.model.User;
import com.ljh.ljhclientsdk.utils.SignUtil;

import java.util.HashMap;
import java.util.Map;

import static com.ljh.ljhclientsdk.utils.Constant.GATEWAY_HOST;
import static com.ljh.ljhclientsdk.utils.Constant.GET_BODY;


/**
 * 调用第三方接口客户端
 *
 * @author ljh
 */


public class LjhClient {

    private String accessKey;

    private  String secretKey;



    public LjhClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result = HttpUtil.get(GATEWAY_HOST+"/api/name/", paramMap);
        System.out.println(result);

        return result;

    }

    public String getNameByPost(String name){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result = HttpUtil.get(GATEWAY_HOST+ "/api/name/", paramMap);
        System.out.println(result);

        return result;

    }


    /**
     * 生成post请求头信息集合
     *
     * @param body
     * @return
     */
    private   Map<String, String> getHeader(String body){
        Map<String, String> hashMap = new HashMap<>();

        hashMap.put("accessKey", accessKey);
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(5));
        hashMap.put("body", body);
        hashMap.put("timesTamp", String.valueOf(System.currentTimeMillis() / 1000));

        hashMap.put("sign", SignUtil.getSign(body, secretKey));
        return hashMap;
    }

    /**
     * 生成get请求头信息集合
     *
     * @param
     * @return
     */
    private   Map<String, String> getHeaderByGet(){
        Map<String, String> hashMap = new HashMap<>();

        hashMap.put("accessKey", accessKey);
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(5));

        hashMap.put("timesTamp", String.valueOf(System.currentTimeMillis() / 1000));


        hashMap.put("sign", SignUtil.getSign(GET_BODY, secretKey));
        return hashMap;
    }






    /**
     * 获取用户名字
     * @param user
     * @return
     */
    public  String getUsernameByPost( User user){
        String jsonStr = JSONUtil.toJsonStr(user);
        HttpResponse result = HttpRequest.post(GATEWAY_HOST + "/api/name/user/")
                .addHeaders(getHeader(jsonStr))
                .body(jsonStr)
                .execute();

        System.out.println(result.getStatus());
        System.out.println(result.body());
        return result.body();
    }


    /**
     *随机获取诗词
     * @return
     */

    public String getKnow(){

        String request = HttpUtil.createGet(GATEWAY_HOST+
                "/api/name/three/sentences/interface")
                .addHeaders(getHeaderByGet()).execute().body();



        System.out.println(request);
        return request;

    }



}
