package com.user.config;

import com.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component(value = "myUserDetailService")
public class MyUserDetailService implements UserDetailsService {

    private static Logger logger= LoggerFactory.getLogger(MyUserDetailService.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        logger.info("------loadUserByUsername------="+userName+"=---用户登录了---------------");
        UserDetails userDetails=new User(
                userName,
                userMapper.selectPasswordByUserNameForSecurity(userName),
                AuthorityUtils.commaSeparatedStringToAuthorityList(userMapper.selectRoleByUserNameForSecurity(userName)));

        logger.info(
                        userName+"======="+
                        userMapper.selectPasswordByUserNameForSecurity(userName)+"========="+
                        AuthorityUtils.commaSeparatedStringToAuthorityList(userMapper.selectRoleByUserNameForSecurity(userName))
                );

        return userDetails;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
