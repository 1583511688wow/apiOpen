package com.ljh.project.model.dto.user;


import lombok.Data;

import java.io.Serializable;


@Data
public class UserSettingRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 修改参数
     */

    private String bodyParams;


    private static final long serialVersionUID = 1L;




}
