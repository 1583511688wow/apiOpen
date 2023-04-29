package com.ljh.ljhcommon.model.vo;

import lombok.Data;

import java.util.Date;

/**
 *
 * 返回用户信息
 *
 * @author 李俊豪
 */
@Data
public class UserSettingVo {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;



    /**
     * 账号
     */
    private String userAccount;

    /**
     * 签名 accessKey
     */
    private String accessKey;

    /**
     * 签名 secretKey
     */
    private String secretKey;


    /**
     * 性别
     */
    private Integer gender;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;



}
