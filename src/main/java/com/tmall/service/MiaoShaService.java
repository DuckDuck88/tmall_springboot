package com.tmall.service;

import com.tmall.dao.SeckillGoodsDAO;
import com.tmall.dao.SeckillOrderDAO;
import com.tmall.pojo.SeckillGoods;
import com.tmall.pojo.SeckillOrder;
import com.tmall.pojo.User;
import com.tmall.redis.MiaoShaKey;
import com.tmall.util.MD5Utils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @Program: tmall_springboot
 * @Description: 秒杀
 * @Author: YaYa
 * @Create: 2020-07-04 22:56
 */
@Service
@Log
public class MiaoShaService {
    @Autowired
    SeckillGoodsDAO goodsDAO;
    @Autowired
    SeckillOrderDAO orderDAO;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserService userService;


    //查询秒杀商品
    public List<SeckillGoods> listGoods(){
        return goodsDAO.findAll();
    }

    //查询订单
    public SeckillOrder inquiry(User user,int id){
        return orderDAO.findByUserAndGoods_Id(user,id);
    }

    //查询商品
    public SeckillGoods find(int id){
        return goodsDAO.findSeckillGoodsById(id);
    }

    //减库存
    private boolean reduceStock(int goodsID){
        SeckillGoods good=goodsDAO.findSeckillGoodsById(goodsID);
        int stock=good.getStock();
        log.warning(goodsID+"库存为"+stock);
        if (stock>0){
            good.setStock(stock-1);
            goodsDAO.save(good);
            return true;
        }
        else{
            return false;
        }
    }

    //下订单   减库存  下订单
    @Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = "Exception")
    public boolean addOrder(User user, int id){
        SeckillOrder seckillOrder=new SeckillOrder();
        SeckillGoods goods;
        if (reduceStock(id)){
            goods=goodsDAO.findSeckillGoodsById(id);
        }else {
            log.warning("减库存失败");
            //设置秒杀结束标记
            setGoodsOver(id);
            return false;
        }
        seckillOrder.setUser(user);
        seckillOrder.setGoods(goods);
        orderDAO.save(seckillOrder);
        log.info("保存成功");
        return true;
    }

    //创建Path
    public String createMiaoshaPath(User user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String str = MD5Utils.md5(UUID.randomUUID().toString().replace("-","")+"123456");
        redisTemplate.opsForValue().set(MiaoShaKey.getMiaoshaPath+""+user.getName() + "_"+ goodsId, str);
        return str;
    }

    //检查Path
    public boolean checkPath(User user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = (String) redisTemplate.opsForValue().get(MiaoShaKey.getMiaoshaPath+""+user.getName()+"_"+goodsId);
        return path.equals(pathOld);
    }

    //创建验证码
    public BufferedImage createVerifyCode(User user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisTemplate.opsForValue().set(MiaoShaKey.getMiaoshaVerifyCode, user.getName()+","+goodsId,rnd);
        //输出图片
        return image;
    }

    private static final char[] ops = new char[] {'+', '-', '*'};
    /**
     * 创建验证码需要
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Integer catch1 = (Integer)engine.eval(exp);
            return catch1.intValue();
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

   //-1 秒杀是白  0 排队中  1成功
    public int getMiaoshaResult(int userName, int goodId) {
        SeckillOrder order = inquiry(userService.getById(userName),goodId);
        if(order != null) {
            //秒杀成功，返回订单编号
            return order.getId();
        }else {
            //查询redis中商品是否秒杀结束标记
            boolean isOver = getGoodsOver(goodId);
            if(isOver) {
                //结束则返回失败
                return -1;
            }else {
                return 0;
            }
        }
    }

    //获取redis中是否秒杀结束标记
    private boolean getGoodsOver(int goodId) {
        return redisTemplate.hasKey(""+MiaoShaKey.isGoodsOver+goodId);
    }
    //在redis中设置秒杀结束
    public void setGoodsOver(int goodsId) {
        redisTemplate.opsForValue().set(""+MiaoShaKey.isGoodsOver+goodsId, true);
    }
}
