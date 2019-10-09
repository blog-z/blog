package com.user.aspect;

import com.dubbo.commons.ServerResponse;
import com.user.entity.User;
import com.user.utils.JedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ControllerAspect {

    @Pointcut("execution(* com.user.service.UserService.login(..))")
    public void pointcut(){}

    /**
     *
     * @param proceedingJoinPoint
     * ProceedingJoinPoint继承JoinPoint子接口，它新增了两个用于执行连接点方法的方法： 
     *  java.lang.Object proceed() throws java.lang.Throwable：通过反射执行目标对象的连接点处的方法； 
     *  java.lang.Object proceed(java.lang.Object[] args) throws java.lang.Throwable：通过反射执行目标对象连接点处的方法，不过使用新的入参替换原来的入参。 
     *
     * @param joinPoint
     *  java.lang.Object[] getArgs()：获取连接点方法运行时的入参列表； 
     *  Signature getSignature() ：获取连接点的方法签名对象； 
     *  java.lang.Object getTarget() ：获取连接点所在的目标对象； 
     *  java.lang.Object getThis() ：获取代理对象本身；
     *
     * @return Object
     */
    @Around(value = "pointcut()", argNames = "proceedingJoinPoint,joinPoint")
    public Object check(ProceedingJoinPoint proceedingJoinPoint, JoinPoint joinPoint) throws Throwable {
        Object[] joinPointArgs = joinPoint.getArgs();
        String userName=(String) joinPointArgs[0];
        String userEmail=(String) joinPointArgs[1];
        User user= JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,userEmail);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        return proceedingJoinPoint.proceed();
    }
}
