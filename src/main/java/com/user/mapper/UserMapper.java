package com.user.mapper;

import com.user.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(String userId);

    int insert(User record);

    User selectByPrimaryKey(String userId);

    List<User> selectAll();

    int updateByPrimaryKey(User record);

    int selectByUserNameAndEmail(@Param(value = "userName") String userName, @Param(value = "userEmail") String userEmail);

    User selectByUserNameOrEmail(@Param(value = "userName") String userName, @Param(value = "userPassword") String userPassword);

    String selectPasswordByUserNameForSecurity(String userName);

    String selectRoleByUserNameForSecurity(String userName);
}