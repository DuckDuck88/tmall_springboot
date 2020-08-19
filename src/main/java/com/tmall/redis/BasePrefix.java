package com.tmall.redis;

/**
 * @Program: tmall_springboot
 * @Description:
 * @Author: YaYa
 * @Create: 2020-08-19 17:52
 */
public abstract class BasePrefix implements  KeyPrefix {

    private final int expireSeconds;

    private final String prefix ;

    public BasePrefix(int expireSeconds ,  String prefix ){

        this.expireSeconds = expireSeconds ;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix) {
        this(0,prefix);
    }

    @Override
    public int expireSeconds() {//默认0代表永远过期
        return expireSeconds;
    }

    /**
     * 可确定获取唯一key
     * @return
     */
    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":" +prefix;
    }
}
