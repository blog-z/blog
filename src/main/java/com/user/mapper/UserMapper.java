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

    //注册时检查     用户名/邮箱/电话   有没有被使用
    int selectByUserNameOrEmailOrPhone(@Param(value = "userName") String userName,@Param(value = "userEmail") String userEmail,@Param(value = "Phone") String Phone);

    User selectByUserNameOrEmail(@Param(value = "userName") String userName, @Param(value = "userEmail") String userEmail);

    String selectPasswordByUserNameForSecurity(String userName);

    String selectRoleByUserNameForSecurity(String userName);

    //通过userEmail查找用户
    User selectByUserEmail(String userEmail);

    int checkUserNameOrEmail(@Param(value = "userName") String userName,@Param(value = "userEmail") String userEmail);
}