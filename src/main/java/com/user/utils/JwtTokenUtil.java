package com.user.utils;

import com.user.config.SecurityUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Descrition jwt工具类
 * 提供 jwt token的生成、验证、从token中获取用户信息
 */

@Component
public class JwtTokenUtil {

    /**
     * JWT签名加密key
     */
    public static String secret;
    /**
     * token分割
     */
    public static String tokenPrefix;
    /**
     * token参数头
     */
    public static String tokenHeader;
    /**
     * 权限参数头
     */
    public static String authHeader;
    /**
     * token有效期
     */
    public static Integer tokenExpireTime;


    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        JwtTokenUtil.secret = secret;
    }
    @Value("${jwt.tokenPrefix}")
    public void setTokenPrefix(String tokenPrefix) {
        JwtTokenUtil.tokenPrefix = tokenPrefix;
    }
    @Value("${jwt.tokenHeader}")
    public void setTokenHeader(String tokenHeader) {
        JwtTokenUtil.tokenHeader = tokenHeader;
    }
    @Value("${jwt.authHeader}")
    public void setAuthHeader(String authHeader) {
        JwtTokenUtil.authHeader = authHeader;
    }
    @Value("${jwt.expiration}")
    public void setTokenExpireTime(Integer tokenExpireTime) {
        JwtTokenUtil.tokenExpireTime = tokenExpireTime;
    }


    public static String getToken(UserDetails userDetails){
        SecurityUserDetails securityUserDetails=(SecurityUserDetails)userDetails;
        //登录成功后生成token
        String token= Jwts.builder()
                            //主题，放入用户名
                            .setSubject(securityUserDetails.getMyUsername())
                            .setId(securityUserDetails.getUserId())
                            //放入用户权限
                            .claim(authHeader,JsonUtil.objToString(securityUserDetails.getAuthorities()))
                            //失效时间
                            .setExpiration(new Date(System.currentTimeMillis()+tokenExpireTime*60*1000))
                            //签名算法和密钥
                            .signWith(SignatureAlgorithm.HS256,secret)
                            .compact();
        return tokenPrefix+token;
    }
}
