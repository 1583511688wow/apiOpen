package com.ljh.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljh.ljhcommon.model.entity.InterfaceInfo;


/**
* @author 李俊豪
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-03-13 21:45:42
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

}
