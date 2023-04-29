package com.ljh.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljh.ljhcommon.model.entity.User;

/**
 * @Entity com.ljh.project.model.model.entity.User
 */
public interface UserMapper extends BaseMapper<User> {


    long selectUserIdByPhone(String userPhone);




}




