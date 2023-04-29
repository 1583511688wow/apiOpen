package com.ljh.project.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ljh.ljhcommon.model.entity.InterfaceInfo;
import com.ljh.ljhcommon.model.entity.User;
import com.ljh.ljhcommon.model.entity.UserInterfaceInfo;
import com.ljh.project.common.ErrorCode;
import com.ljh.project.exception.BusinessException;
import com.ljh.project.mapper.InterfaceInfoMapper;
import com.ljh.project.mapper.UserInterfaceInfoMapper;
import com.ljh.project.mapper.UserMapper;
import com.ljh.project.service.InterfaceInfoService;
import com.ljh.project.service.UserInterfaceInfoService;
import com.ljh.project.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ljh.project.constant.UserConstant.INTERFACE_COUNT;


@Component
public class InterFaceUtils {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;


    @Resource
    private UserMapper userMapper;

    @Resource
    private RedissonClient redissonClient;



    /**
     *
     * 默认每天给用户分配10次调用接口的次数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void interfaceCount() {

        RLock lock = redissonClient.getLock("api:interface:lock");
        //更新调用接口次数
        try {
            //只有一个线程能取到锁
            if (lock.tryLock(0,-1, TimeUnit.MINUTES)) {

                boolean isInterfaceCount = userInterfaceInfoMapper.interfaceCount(INTERFACE_COUNT);
                System.out.println("getLock:" + Thread.currentThread().getId());

            }
        } catch (Exception e) {

            new BusinessException(ErrorCode.OPERATION_ERROR);
        } finally {

            //只能释放线程自己的锁
            if (lock.isHeldByCurrentThread()){

                lock.unlock();
            }

        }


    }








}