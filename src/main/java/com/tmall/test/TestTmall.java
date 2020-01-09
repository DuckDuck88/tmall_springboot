package com.tmall.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*
* 数据库测试类
* 用于向数据库插入测试数据
* */
public class TestTmall {

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (
                Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tmall_springboot?useUnicode=true&characterEncoding=utf8",
                        "root", "admin");
                Statement s = c.createStatement()
        ) {
//            for (int i = 1; i <= 10; i++) {
//                String sqlFormat = "insert into user values (null, '测试分类%d','password%d',null)";
//                String sql = String.format(sqlFormat, i, i);
//                s.execute(sql);
//            }
//
//            System.out.println("已经成功创建10条用户测试数据");
            String sql1="insert INTO order_ VALUES (1,'201608241638122609867','某某市，某某区，某某街道，某某号 ','610000','某某某','15111111111',NULL,'2016-12-30',NULL,NULL,NULL,14,'waitDelivery')";
            String sql2="insert INTO orderitem VALUES (1,2,1,14,2)";
            String sql3="insert INTO orderitem VALUES (2,3,1,14,2)";
            s.execute(sql1);
            s.execute(sql2);
            s.execute(sql3);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}