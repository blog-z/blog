package com.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    @Qualifier(value = "myUserDetailService")
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {  //方法中各个参数有严格的顺序规定


        http.authorizeRequests()
                .antMatchers("/","/*.html","favicon.ico","css/**","js/**","/fonts/**","/img/**"
                        ,"/pages/**","/druid/**","/static/**").permitAll();

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/register").permitAll()
                .antMatchers("/user/**").hasAnyRole("0","1")
                .and()
                .formLogin().loginPage("/user/login").loginProcessingUrl("/user/login").usernameParameter("userName").passwordParameter("userPassword").successForwardUrl("/user/getUserMessage").permitAll()
                .and()
                .rememberMe()
                .and()
                .sessionManagement().maximumSessions(1).expiredUrl("/user/login");
        //基于token,所以不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }



    //对前端传过来的密码选择加密方式，推荐使用bcrypt加密方式
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder MyBCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
