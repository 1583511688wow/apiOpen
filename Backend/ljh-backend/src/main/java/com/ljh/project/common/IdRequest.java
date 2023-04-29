package com.ljh.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用请求(封装id)
 *
 * @author ljh
 */
@Data
public class IdRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}