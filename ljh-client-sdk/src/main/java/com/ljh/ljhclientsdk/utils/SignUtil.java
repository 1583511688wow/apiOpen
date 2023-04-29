package com.ljh.ljhclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import org.apache.commons.lang3.StringUtils;

import static com.ljh.ljhclientsdk.utils.Constant.GET_BODY;



public class SignUtil {


    /**
     *生成签名
     * @param body
     * @param secretKey
     * @return
     */
    public static String getSign(String body, String secretKey) {

        Digester digester = new Digester(DigestAlgorithm.SHA256);


        if (StringUtils.isBlank(body)){

            String content = GET_BODY + "." + secretKey;

            return digester.digestHex(content);

        }
        String content = body + "." + secretKey;


        return digester.digestHex(content);
    }




}
