package com.ljh.project.controller;


import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ljh.ljhcommon.model.entity.InterfaceInfo;
import com.ljh.ljhcommon.model.entity.UserInterfaceInfo;
import com.ljh.project.annotation.AuthCheck;
import com.ljh.project.common.BaseResponse;
import com.ljh.project.common.ErrorCode;
import com.ljh.project.common.ResultUtils;
import com.ljh.project.exception.BusinessException;
import com.ljh.project.mapper.UserInterfaceInfoMapper;
import com.ljh.project.model.vo.InterfaceInfoVO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 分析控制器
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisControllerAnalysisController {


    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private com.ljh.project.service.InterfaceInfoService interfaceInfoService;


    @GetMapping("//top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo(){

        List<UserInterfaceInfo> interfaceInfoVOList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        Map<Long, List<UserInterfaceInfo>> listMap = interfaceInfoVOList
                .stream().collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", listMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);

        if (CollectionUtils.isEmpty(list)){
            new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        List<InterfaceInfoVO> infoVOList = list.stream().map(interfaceInfo ->

                {
                    InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
                    BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
                    int totalNum = listMap.get(interfaceInfo.getId()).get(0).getTotalNum();
                    interfaceInfoVO.setTotalNum(totalNum);


                    return interfaceInfoVO;
                }
        ).collect(Collectors.toList());


        return ResultUtils.success(infoVOList);
    }


}
