package com.tmall.redis;

/**
 * @ClassName KeyPrifix
 * @Description redis中存放key的前缀
 * @Author 是哪的鸭
 * @Date
 * @Version 1.0
 */
public interface KeyPrefix {

    int expireSeconds() ;

    String getPrefix() ;

}
