# tmall_springboot
开发环境：JDK8、MySQL 5.5.15、IDEA 2019、Redis 3.2.1 RabbitMQ 3.7.18  
仿照天猫的Springboot项目，实现天猫基本功能，包括商品秒杀
本项目已经部署到云服务器，点击如下链接访问http://119.3.190.216:8080/MyTmall    
！！！部署只为学习交流使用，请勿使用支付功能！！！

### 已完成的基本功能如下：
界面仿照天猫，实现了天猫网站的基本功能
用户：注册、登陆、搜索商品、加入购物车、购买、结算购物车、提交订单、确认收货、评价
管理员：管理商品分类、管理商品各种信息、查看和管理订单信息、查看用户

### 计划   
项目大体内容目前基本完成，之后会进行改良：
1. 使用Redis缓存热点数据（已完成）
2. 使用Ngnix进行反向代理和负载均衡(已完成)
3. 加入秒杀活动。（秒杀核心逻辑完成）
    - 接口限流 令牌桶算法
    - redis预减库存
    - 内存标记
    - mq异步下单
4. 使用Srping Session实现Session共享。
5. 使用ElasticSearch搜索引擎技术实现全文商品搜索
6. 使用Spring Cloud将项目重构微服务架构并进行容器化部署。

如有相关交流问题请联系本人：
QQ：1307317886  email：1307317886@qq.com  
