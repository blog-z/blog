package com.user.config;

import com.user.filter.JWTAuthenticationFilter;
import com.user.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 需要放行的URL
     */
    private static final String[] AUTH_WHITELIST = {
            "/user/login",
            "/user/register",
            "/user/getQuestion",
            "/user/setAnswer",
            "/user/setPassword",
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/ueditor/**",
            "**/favicon.ico",
            // other public endpoints of your API may be appended to this array
    };


    @Autowired
    @Qualifier(value = "myUserDetailService")
    private UserDetailsService userDetailsService;


    @Autowired
    private MyAuthenticationEntryPointHandler myAuthenticationEntryPointHandler;
    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;
    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private MyAuthenticationFailHandler myAuthenticationFailHandler;
    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {  //方法中各个参数有严格的顺序规定
        //关闭跨站请求防护
        http
                .csrf().disable()
                //基于token,所以不要session;如果基于session则使用这段代码
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //对请求进行认证
                //url认证顺序为:1.先配置放行需要认证的permitAll() 2.然后配置需要特定权限的hasRole() 3.最后配置anyRequest().authenticated()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                //其他请求都需要进行认证，认证通过才能访问
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                //在访问一个受保护的资源，用户没有通过登录认证，则抛出登录认证异常，MyAuthenticationEntryPointHandler类中commence()就会调用
                .authenticationEntryPoint(myAuthenticationEntryPointHandler)
                //在访问一个受保护的资源，用户通过了登录认证，但是权限不够，抛出授权异常，在myAccessDeniedHandler中处理
                .accessDeniedHandler(myAccessDeniedHandler)
                .and()
                .formLogin()
                //登录url 和controller无关系
                .loginProcessingUrl("/user/login")
                .usernameParameter("userName").passwordParameter("userPassword")
                .permitAll()
                //登录成功后 MyAuthenticationSuccessHandler类中onAuthenticationSuccess（）被调用
                .successHandler(myAuthenticationSuccessHandler)
                //登录失败后 MyAuthenticationFailureHandler 类中onAuthenticationFailure（）被调用
                .failureHandler(myAuthenticationFailHandler)
                .and()
                .logout()
                //退出系统的url
                .logoutUrl("/user/logout")
                //退出系统后跳转的url
                .logoutSuccessUrl("/")
                //退出系统后的业务处理
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .permitAll();

        //增加JWT过滤器  除/login外，其他请求都要经过此过滤器
        http.addFilter(new JWTAuthenticationFilter(authenticationManager()));

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
