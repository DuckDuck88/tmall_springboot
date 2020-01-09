package com.tmall.web;

import com.tmall.pojo.Category;
import com.tmall.service.CategoryService;
import com.tmall.util.ImageUtil;
import com.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 本类用来专门提供Restful服务器控制器
 * 业务逻辑上调用CategoryService的list方法
 * 返回Page4Navigator类型，并通过RestController转化为json数据类型
 *
 */

@RestController
public class CategoryController {
    @Autowired //自动装配
    CategoryService categoryService;

    //查询分页
    @GetMapping(value = "/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start",defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size)
            throws Exception {
        start=start<0?0:start;
        Page4Navigator<Category> page=categoryService.list(start, size, 5);//5表示导航分页最多有5个
        return page;
    }

    //增加
    @PostMapping(value = "/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request)
            throws Exception{
        categoryService.add(bean);
        saveOrUpdateImageFile(bean,image,request);
        return bean;
    }

    //设置图片函数，用来新增或者修改
    public void  saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
            throws  IOException {
        File imageFolder=new File(request.getServletContext().getRealPath("img/category"));
        File file=new File(imageFolder,bean.getId()+".jpg");
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        image.transferTo(file);
        BufferedImage img= ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

    //删除
    @DeleteMapping(value = "/categories/{id}")
    public String delete(@PathVariable("id") int id,HttpServletRequest request){
        categoryService.delete(id);
        File imageFolder=new File(request.getServletContext().getRealPath("img/category"));
        File file=new File(imageFolder,id+".jpg");
        file.delete();
        return null;
    }

    //根据id获取Category
    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id)throws Exception{
        Category bean=categoryService.get(id);
        return bean;
    }

    @PutMapping("/categories/{id}")
    public Object update(Category bean,MultipartFile image,HttpServletRequest request) throws IOException {
        String name=request.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);
        if (image!=null){
            saveOrUpdateImageFile(bean, image, request);
        }
        return bean;
    }
}
