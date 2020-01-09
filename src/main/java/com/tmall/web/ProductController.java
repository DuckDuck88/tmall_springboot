package com.tmall.web;

import com.tmall.pojo.Product;
import com.tmall.service.CategoryService;
import com.tmall.service.ProductImageService;
import com.tmall.service.ProductService;
import com.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;

    //查询、分页
    @GetMapping(value = "/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid,
                                        @RequestParam(value = "start",defaultValue = "0")int start,
                                        @RequestParam(value = "size",defaultValue = "5")int size)
            throws Exception{
        start=start>0?start:0;
        Page4Navigator<Product> page=productService.list(cid, start, size, 5);
        productImageService.setFirstProdutImages(page.getContent());
        return page;
    }

    @GetMapping(value = "/products/{id}")
    public Product get(@PathVariable("id")int id){
        return productService.get(id);
    }
    //删除
    @DeleteMapping(value = "/products/{id}")
    public String delete(@PathVariable("id") int id){
        productService.delete(id);
        return null;
    }
    //增加
    @PostMapping(value = "/products")
    public Product add(@RequestBody Product bean){
        bean.setCreateDate(new Date());
        productService.add(bean);
        return bean;
    }
    @PutMapping(value = "/products")
    public Product update(@RequestBody Product bean){
        productService.update(bean);
        return bean;
    }

}
