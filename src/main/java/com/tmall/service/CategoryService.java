package com.tmall.service;

import com.tmall.dao.CategoryDAO;
import com.tmall.pojo.Category;
import com.tmall.pojo.Product;
import com.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 创建sort对象，调用categoryDAO对数据排序
 */
@Service
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    //分页函数
    public Page4Navigator<Category> list(int start,int size,int navigatePages){
        Pageable pageable = PageRequest.of(start, size, Sort.Direction.ASC,"id");
        Page pageFromJPA =categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //查询列表
    public List<Category> list() {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        return categoryDAO.findAll(Sort.by(order));
    }

    //增加
    public void add(Category bean){
        categoryDAO.save(bean);
    }

    //删除
    public void delete(int id){
        categoryDAO.deleteById(id);
    }

    //获取
    public Category get(int id){
        Category category=categoryDAO.getOne(id);
        return category;
    }

    //更新数据
    public void update(Category bean){
        categoryDAO.save(bean);
    }


    //去掉product里面的分类属性。转化为json时会遍历product，product里包含category属性，无限循环
    //增加此方法后，无法实现从产品中获取分类属性的业务
    public void removeCategoryFromProduct(List<Category> categories){
        for (Category category:categories){
            removeCategoryFromProduct(category);
        }
    }
    public void removeCategoryFromProduct(Category category){
        List<Product> products=category.getProducts();
        if(null!=products){
            for (Product product:products){
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if(null!=productsByRow){
            for (List<Product> product:productsByRow){
                for (Product p:product){
                    p.setCategory(null);
                }
            }
        }
    }

}
