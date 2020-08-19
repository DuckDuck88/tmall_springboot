package com.tmall.redis;

/**
 * @Program: tmall_springboot
 * @Description:
 * @Author: YaYa
 * @Create: 2020-08-19 17:50
 */
public class MiaoShaKey extends BasePrefix {

    private MiaoShaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoShaKey isGoodsOver = new MiaoShaKey(0, "go");
    //秒杀路径
    public static MiaoShaKey getMiaoshaPath = new MiaoShaKey(60, "mp");
    //验证码
    public static MiaoShaKey getMiaoshaVerifyCode = new MiaoShaKey(300, "vc");

}

