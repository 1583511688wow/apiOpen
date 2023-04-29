package com.ljh.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 手机登入请求
 *
 */

@Data
public class PhoneRequest implements Serializable {

    /**
     *
     *用户手机号
     */

    private String userPhone;

    /**
     *
     * 验证码
     */

    private String code;



    private static final long serialVersionUID = 1L;




}
