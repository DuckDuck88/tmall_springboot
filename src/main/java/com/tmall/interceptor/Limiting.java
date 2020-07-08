package com.tmall.interceptor;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

/**
 * @Program: tmall_springboot
 * @Description: 令牌桶
 * @Author: YaYa
 * @Create: 2020-07-06 13:38
 */
@Component
public class Limiting {

    private final double rate=1000d;
    private  final RateLimiter rateLimiter=RateLimiter.create(rate);

    public boolean tryAcquire(){
        return rateLimiter.tryAcquire();
    }
}
