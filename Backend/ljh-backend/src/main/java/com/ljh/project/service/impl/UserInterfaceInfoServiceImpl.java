package com.ljh.project.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljh.ljhcommon.model.entity.UserInterfaceInfo;
import com.ljh.project.common.ErrorCode;
import com.ljh.project.exception.BusinessException;
import com.ljh.project.mapper.UserInterfaceInfoMapper;
import com.ljh.project.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
* @author 李俊豪
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-03-30 11:00:36
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {


    /**
     *接口调用校验
     *
     * @param userInterfaceInfo
     * @param add
     */
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {

            if (userInterfaceInfo == null){

                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

            if (add){
                if (userInterfaceInfo.getLeftNum() < 0  ){

                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"剩余次数不能小于0!");
                }


                if (userInterfaceInfo.getId() <= 0 || userInterfaceInfo.getUserId() <= 0) {

                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误!");

                }


                }

            }


    /**
     * 统计接口调用次数
     *
      * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {


        if (interfaceInfoId <=0 || userId <=0 ){

            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误!");

        }


        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();

        updateWrapper.eq("userId", userId).eq("interfaceInfoId", interfaceInfoId)
                .setSql("leftNum = leftNum - 1, totalNum = totalNum +1");



        return this.update(updateWrapper);
    }
}




