package com.user.aspect;

import com.dubbo.commons.Const;
import com.dubbo.commons.ServerResponse;
import com.user.utils.JedisUtil;
import com.user.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class ControllerArticleAspect {

    @Pointcut(value = "execution(* com.user.controller.ArticleController.deleteArticle(..))")
    public void pointcut(){}

    @Around(value = "pointcut()")
    public Object deleteArticle(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] proceedingJoinPointArgs = proceedingJoinPoint.getArgs();
        HttpServletRequest httpServletRequest=(HttpServletRequest) proceedingJoinPointArgs[0];
        String userName=(String) proceedingJoinPointArgs[1];
        String articleId=(String) proceedingJoinPointArgs[2];
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        //根据文章ID查出此文章所属用户ID，比较ID是否相同
        String articleUserId= JedisUtil.getValue(Const.RedisKey.BeforeArticleKeyId+articleId);
        if (!JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId().equals(articleUserId)){
            return ServerResponse.createByErrorMessage("不要删除别人的文章");
        }
        return proceedingJoinPoint.proceed();
    }

    private Boolean checkRole(HttpServletRequest httpServletRequest, String userName){
        String token = httpServletRequest.getHeader(JwtTokenUtil.tokenHeader);
        if (token!=null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(JwtTokenUtil.secret)
                    .parseClaimsJws(token.replace(JwtTokenUtil.tokenPrefix, ""))
                    .getBody();
            //相等jwt中token和用户名一致
            return claims.getSubject().equals(userName);
        }
        return false;
    }
}
