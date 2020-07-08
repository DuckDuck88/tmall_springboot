package com.tmall.web;

import com.tmall.conparator.ProductAllComparator;
import com.tmall.conparator.ProductDateComparator;
import com.tmall.conparator.ProductPriceComparator;
import com.tmall.conparator.ProductReviewComparator;
import com.tmall.conparator.ProductSaleCountComparator;
import com.tmall.pojo.Category;
import com.tmall.pojo.Order;
import com.tmall.pojo.OrderItem;
import com.tmall.pojo.Product;
import com.tmall.pojo.ProductImage;
import com.tmall.pojo.PropertyValue;
import com.tmall.pojo.Review;
import com.tmall.pojo.User;
import com.tmall.service.CategoryService;
import com.tmall.service.OrderItemService;
import com.tmall.service.OrderService;
import com.tmall.service.ProductImageService;
import com.tmall.service.ProductService;
import com.tmall.service.PropertyValueService;
import com.tmall.service.ReviewService;
import com.tmall.service.UserService;
import com.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
* 前台功能控制器
* */
@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderService orderService;

//
//     查询所有分类
//     为分类填充产品
//     填充推荐产品集合
//     移除产品中的分类信息，避免循环递归
    @GetMapping("/forehome")
    public Object home(){
        List<Category> categories=categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        categoryService.removeCategoryFromProduct(categories);
        return categories;
    }

    /*
    * 注册功能，通过shrio进行安全管理
    * */
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user){ //通过参数User获取浏览器提交的账号密码
        String name=user.getName();
        String password=user.getPassword();
        name= HtmlUtils.htmlEscape(name);           //htmlEscape(name)对账号里的特殊符号进行转义
        user.setName(name);

        boolean exist=userService.isExist(name);    //判断用户名是否存在

        if(exist){
            String message="起名字好难鸭，这个名字已经有人用了哦";
            return Result.fail(message);
        }

        String salt=new SecureRandomNumberGenerator().nextBytes().toString();//注册账户时随机产生salt
        int times=2;                //设置加密次数
        String algorithmName="md5"; //设置加密算法
        String encodedPassword=new SimpleHash(algorithmName,password,salt,times).toString(); //对密码进行加密

        user.setSalt(salt);
        user.setPassword(encodedPassword);
        userService.add(user);

        return Result.success();
    }

    /*
    * 登陆
    * 登陆时通过Shiro的方式进行校验
    * */
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam,HttpSession session){
        String name=userParam.getName();
        name= HtmlUtils.htmlEscape(name);

        Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(name,userParam.getPassword());
        try{
            subject.login(token);
            User user=userService.getByName(name);
            session.setAttribute("user", user);
            return Result.success();
        }catch (AuthenticationException e){
            String message="账号密码错误";
            return Result.fail(message);
        }
    }

    //检查是否登录
    @GetMapping(value = "forecheckLogin")
    public Object checkLogin( ) {
        Subject subject=SecurityUtils.getSubject();
        if (subject.isAuthenticated())
            return Result.success();
        else
            return Result.fail("未登录");
    }


    //展示商品的详情，价格、销量、评论、图片等，将他们放入map中
    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable(value = "pid")int pid){
        Product product=productService.get(pid);
        List<ProductImage> productSingleImages= productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages= productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        List<PropertyValue> pvs=propertyValueService.list(product);
        List<Review> reviews=reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProdutImage(product);
        //将产品的所有信息放入map中传到前端
        Map<String,Object> map=new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);
        return Result.success(map);
    }

    //查询分类下所有商品和商品的基本信息（销量、图片、名字等）
    @GetMapping(value = "forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort){
        Category c=categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);
        //根据sort值判断使用哪种排序
        if(null!=sort){
            switch (sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;
                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;
                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;
                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }
        return c;
    }

    @PostMapping(value = "foresearch")
    public Object search(String keyword){
        if(null==keyword)
            keyword="";
        List<Product> ps=productService.search(keyword,0,20);
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    //购买
    @GetMapping(value = "forebuyone")
    public Object bugone(int pid,int num,HttpSession session){
        return buyOneAndAddCart(pid,num,session);
    }

    //添加购物车
    @GetMapping(value = "foreaddCart")
    public Object addCart(int pid,int num,HttpSession session){
        buyOneAndAddCart(pid, num, session);
        return Result.success();
    }

    private int buyOneAndAddCart(int pid, int num, HttpSession session) {
        Product product=productService.get(pid);
        int oiid=0;  //存储订单项id
        User user= (User) session.getAttribute("user");
        boolean found=false;
        List<OrderItem> ois=orderItemService.listByUser(user);
        //如果已经存在相同产品的订单项，增加他的数量
        for(OrderItem oi:ois){
            if(product.getId()==oi.getProduct().getId()){
                oi.setNumber(num+oi.getNumber());
                orderItemService.update(oi);
                found=true;
                oiid=oi.getId();
                break;
            }
        }
        //如果不存在，创建新的订单项
        if (!found){
            OrderItem oi=new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid=oi.getId();
        }
        return oiid;
    }

    //结算页面
    @GetMapping("forebuy")
    public Object buy(String[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;
        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        session.setAttribute("ois", orderItems);
        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }



    //根据用户返回和这个用户关联的订单项
    @GetMapping(value = "forecart")
    public Object cart(HttpSession session){
        User user= (User) session.getAttribute("user");
        List<OrderItem> ois=orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(ois);
        return ois;
    }

    //购物车中调整数量
    @GetMapping(value = "forechangeOrderItem")
    public Object changeOrderItem(HttpSession session, int pid, int num){
        User user= (User) session.getAttribute("user");
        if(null==user){
            return Result.fail("没有登录呢");
        }
        List<OrderItem> ois=orderItemService.listByUser(user);
        for(OrderItem oi:ois){
            oi.setNumber(num);
            orderItemService.update(oi);
            break;
        }
        return Result.success();
    }

    //购物车中删除订单物品
    @GetMapping(value = "foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user= (User) session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }

    //结算订单时执行以下操作
    @PostMapping(value = "forecreateOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");

        float total =orderService.add(order,ois);

        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }

    //购买成功后更新订单信息
    @GetMapping(value = "forepayed")
    public Object payed(int oid){
        Order order=orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    //查询我的订单
    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }

    //确认收货
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);//填充订单项
        orderService.cacl(o);
        orderService.removeOrderFromOrderItem(o);//移除订单项上的订单属性，否则会重复递归
        return o;
    }

    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
       //修改对象o的状态为等待评价，修改其确认支付时间
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }

    //仅设置对订单中第一个产品进行评价
    /*
    *  获取参数oid
    *   根据oid获取订单对象o
    *   为订单对象填充订单项
    *   获取这个产品的评价集合
    *   为产品设置评价数量和销量
    *   把产品，订单和评价集合放在map上
    * */
    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p);
        productService.setSaleAndReviewNumber(p);
        Map<String,Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }
    /*
    *   修改订单对象状态
    *   根据pid获取产品对象
    *   获取参数content (评价信息)
    *   对评价信息进行转义，道理同注册ForeRESTController.register()
    *   创建评价对象review
    *    为评价对象review设置 评价信息，产品，时间，用户
    */
    @PostMapping("foredoreview")
    public Object doreview( HttpSession session,int oid,int pid,String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user =(User)  session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }
}
