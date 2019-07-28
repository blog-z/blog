package com.user.filter;

import com.user.commons.ServerResponse;
import com.user.utils.JsonUtil;
import com.user.utils.JwtTokenUtil;
import com.user.utils.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager){
        super(authenticationManager);
    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint){
        super(authenticationManager,authenticationEntryPoint);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //获取accessToken
        String accessToken=request.getHeader(JwtTokenUtil.tokenHeader);
        if (accessToken!=null&&accessToken.startsWith(JwtTokenUtil.tokenPrefix)){
            UsernamePasswordAuthenticationToken authenticationToken=getAuthentication(request,response);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request,response);
        return;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(JwtTokenUtil.tokenHeader);
        if (!StringUtils.isEmpty(token)) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(JwtTokenUtil.secret)
                        .parseClaimsJws(token.replace(JwtTokenUtil.tokenPrefix, ""))
                        .getBody();

                //获取用户名
                String username = claims.getSubject();
                String userId=claims.getId();
                //获取权限（角色）
                List<GrantedAuthority> authorities = new ArrayList<>();
                String authority = claims.get(JwtTokenUtil.authHeader).toString();
                if(!StringUtils.isEmpty(authority)){
                    //authority="[{"authority":"common"}]"
                    List<Map<String,String>> authorityMap = JsonUtil.stringToObj(authority,List.class);

                    for(Map<String,String> role : authorityMap){
                        if(!StringUtils.isEmpty(role)) {
                            authorities.add(new SimpleGrantedAuthority(role.get("authority")));
                        }
                    }
                }
                if(!StringUtils.isEmpty(username)) {
                    //此处password不能为null
                    User principal = new User(username, "", authorities);
                    return new UsernamePasswordAuthenticationToken(principal, userId, authorities);
                }
            } catch (ExpiredJwtException e) {
                System.out.println("toekn超过有效期，请重新登");
                //throw new BaseException("401","toekn超过有效期，请重新登录");
                ResponseUtil.out(response,ServerResponse.createByErrorMessage("token has expired"));
            } catch (Exception e){
                ResponseUtil.out(response,ServerResponse.createByErrorMessage("token invalid"));
            }
        }
        return null;
    }
}
