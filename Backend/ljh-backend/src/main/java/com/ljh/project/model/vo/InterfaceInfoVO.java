package com.ljh.project.model.vo;

import com.ljh.ljhcommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口信息封装视图
 *
 * @author yupi
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoVO extends InterfaceInfo {

    /**
     * 调用次数
     */
    private Integer totalNum;


    /**
     *
     * 剩余调用次数
     */

    private Integer leftNum;

    private static final long serialVersionUID = 1L;
}