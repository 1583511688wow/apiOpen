package com.ljh.ljhcommon.service;

/**
 *
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     *校验是否还有调用次数
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean checkCount(long interfaceInfoId, long userId);


}
