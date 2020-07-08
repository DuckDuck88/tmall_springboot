package com.tmall.web;

import com.tmall.interceptor.Limiting;
import com.tmall.pojo.SeckillGoods;
import com.tmall.pojo.User;
import com.tmall.service.MiaoShaService;
import com.tmall.util.Result;
import lombok.extern.java.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Program: tmall_springboot
 * @Description: 秒杀类
 * @Author: YaYa
 * @Create: 2020-07-04 18:08
 */
@RestController
@Log
public class MiaoShaController implements InitializingBean {
    @Autowired
    Limiting limiting;
    @Autowired
    MiaoShaService miaoShaService;
    @Autowired
    RedisTemplate redisTemplate;

    //内存标记,用于减少redis访问
    private final Map<Long, Boolean> localOver = new HashMap();

    //系统初始化
    @Override
    public void afterPropertiesSet() throws Exception {
        List<SeckillGoods> list = miaoShaService.listGoods();
        ValueOperations ops = redisTemplate.opsForValue();
        for (SeckillGoods good : list) {
            ops.set(good.getId() + "", good.getStock());
            log.info(good.getId() + good.getName() + "库存为:" + good.getStock());
            localOver.put(good.getId(), false);
        }
    }

    //不走缓存  用户—是否已经购买—判断库存—下订单
    @GetMapping(value = "seckill/{goodId}")
    public Object miaosha(@PathVariable(value = "goodId") int goodId, HttpSession session) {
        if (!limiting.tryAcquire())
            return Result.fail("fail，限流");
        //判断用户
        User user = (User) session.getAttribute("user");
        if (user == null)
            return Result.fail("fail，未登录");
        //判断是否已经买过
        if (miaoShaService.inquiry(user, goodId) != null)
            return Result.fail("fail,已经购买");
        SeckillGoods good = miaoShaService.find(goodId);
        if (good.getStock() <= 0)
            return Result.fail("fail,已经卖完啦");
        //减库存
        if (miaoShaService.addOrder(user, goodId))
            return Result.success("成功");
        else
            return Result.success("fail,下单失败");
    }

    //走缓存  用户--检查内存标记--是否已经购买--预减库存--下订单
    @GetMapping(value = "seckill2/{goodsId}")
    public Object miaosha2(@PathVariable(value = "goodsId") int goodsId, HttpSession session) {
        if (!limiting.tryAcquire()) {
            return Result.fail("fail，限流");
        }
        //判断用户
        User user = (User) session.getAttribute("user");
        if (user == null)
            return Result.fail("fail，未登录");
        //判断是否已经买过
        if (miaoShaService.inquiry(user, goodsId) != null)
            return Result.fail("fail,已经购买过啦");
        //判断内存标记
        if (localOver.get(goodsId))
            return Result.fail("fail,已经卖完啦");
        //判断库存,redis预减库存
        ValueOperations ops = redisTemplate.opsForValue();
        Long stock = ops.decrement(goodsId, 1);
        if (stock <= 0) {
            localOver.put((long) goodsId, true);
            return Result.fail("fail,刚卖完啦");
        }
        if (miaoShaService.addOrder(user, goodsId))
            return Result.success("成功");
        else
            return Result.fail("fail,下单失败");
    }

    //异步下单  用户--检查内存标记--是否购买--预减库存--放入消息队列异步下单
    @GetMapping(value = "seckill3/{goodId}")
    public Object miaosha3(@PathVariable(value = "goodId")int goodId,HttpSession session){

        return Result.success();
    }
}
