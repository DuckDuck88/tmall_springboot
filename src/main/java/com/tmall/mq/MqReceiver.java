package com.tmall.mq;

import com.tmall.pojo.SeckillGoods;
import com.tmall.pojo.User;
import com.tmall.service.MiaoShaService;
import com.tmall.util.String2Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Program: tmall_springboot
 * @Description:
 * @Author: YaYa
 * @Create: 2020-07-07 16:55
 */
@Component
@RabbitListener
public class MqReceiver {
   @Autowired
    MiaoShaService miaoShaService;


    private static final Logger log = LoggerFactory.getLogger(MqReceiver.class);

    //接收消息  判断库存--判断是否购买--下单
    @RabbitListener(queues=MqConfig.MIAO_SHA_QUEUE)
    public void mqReceive(String msg){
        log.info("receive message:"+msg);
        MqMessage mqMessage= String2Bean.stringToBean(msg,MqMessage.class);
        User user=mqMessage.getUser();
        int id=mqMessage.getGoods();
        //判断库存
        SeckillGoods good = miaoShaService.find(id);
        int stock=good.getStock();
        if (stock<=0){
            miaoShaService.setGoodsOver(id);
            return;
        }
        //判断是否购买
        if (miaoShaService.inquiry(user, id)!=null)
            return;
        miaoShaService.addOrder(user, id);
    }

}
