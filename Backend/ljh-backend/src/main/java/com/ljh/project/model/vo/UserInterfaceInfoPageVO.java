package com.ljh.project.model.vo;

import com.ljh.ljhcommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 接口信息封装视图
 *
 * @author yupi
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoPageVO extends InterfaceInfo {

    private Long userId;
    private Long interfaceInfoId;
    private Integer leftNum;
    private Integer status;
    private Integer follow;


    private static final long serialVersionUID = 1L;
}