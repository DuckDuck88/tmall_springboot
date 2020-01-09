package com.tmall.dao;

import com.tmall.pojo.Category;
import com.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product,Integer>{
    Page<Product> findByCategory(Category category, Pageable pageable);//
    List<Product> findByCategoryOrderById(Category category);//通过分类查询产品，无需分页

    List<Product> findByNameLike(String keyword, Pageable pageable);
}
