package com.tmall.service;


import com.tmall.dao.UserDAO;
import com.tmall.pojo.User;
import com.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/*
* 这里不提供增删改功能，交由前台用户操作
* service层只提供查询功能
* */
@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    //查询、分页，用于后台展示
    public Page4Navigator<User> list(int start, int size, int navigatePages){
        Sort sort= Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable= PageRequest.of(start, size, sort);
        Page pageFromJPA=userDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //判断是否已经存在
    public boolean isExist(String name){
        User user=getByName(name);
        return null!=user;
    }
    //通过名字查找，用于注册时检测是否已经存在
    private User getByName(String name){
        return userDAO.findByName(name);
    }
    //添加用户，注册用户
    public void  add(User user){
        userDAO.save(user);
    }

    //通过账号密码查找，用于前台登录
    public User get(String name,String password){
        return userDAO.getByNameAndPassword(name, password);
    }
}
