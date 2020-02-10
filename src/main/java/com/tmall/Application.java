package com.tmall;

import com.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching //启动缓存
public class Application extends SpringBootServletInitializer {

    //检查6379是否启动
    static {
        PortUtil.checkPort(6379, "Redis 服务端", true);
    }

    @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
        return super.configure(builder);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
