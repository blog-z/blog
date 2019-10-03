package com.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class NativeAsyncTaskExecutePool implements AsyncConfigurer {

    private static final Logger log= LoggerFactory.getLogger(NativeAsyncTaskExecutePool.class);

    @Autowired
    private TaskThreadPoolConfig taskThreadPoolConfig;

    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor threadPoolTaskExecutor=new ThreadPoolTaskExecutor();

        //核心线程池大小
        threadPoolTaskExecutor.setCorePoolSize(taskThreadPoolConfig.getCorePoolSize());
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(taskThreadPoolConfig.getMaxPoolSize());
        //队列容量
        threadPoolTaskExecutor.setQueueCapacity(taskThreadPoolConfig.getQueueCapacity());
        //活跃时间
        threadPoolTaskExecutor.setKeepAliveSeconds(taskThreadPoolConfig.getKeepAliveSeconds());
        //线程名字前缀
        threadPoolTaskExecutor.setThreadNamePrefix("MyExecutor-");

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler()  {
            @Override
            public void handleUncaughtException (Throwable arg0, Method arg1, Object... arg2) {
                log.error("=========================="+arg0.getMessage()+"=======================", arg0);
                log.error("exception method:"+arg1.getName());
            }
        };
    }
}
