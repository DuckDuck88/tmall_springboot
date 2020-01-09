package com.tmall.service;

import com.tmall.dao.ProductDAO;
import com.tmall.pojo.Category;
import com.tmall.pojo.Product;
import com.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    //用于后台的方法
    //增
    public void add(Product bean){
        productDAO.save(bean);
    }
    //删
    public void delete(int id){
        productDAO.deleteById(id);
    }
    //改
    public void update(Product bean){
        productDAO.save(bean);
    }
    //查
    public Product get(int id) {
        return productDAO.getOne(id);
    }
    //查询、分页
    public Page4Navigator<Product> list(int cid,int start,int size,int navigatePages){
        Category category=categoryService.get(cid);
        Sort sort=Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable=PageRequest.of(start,size,sort);
        Page<Product> pageFromJPA=productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //用于前台的方法
    //为产品填充产品集合
    public void fill(Category category){
        List<Product> products=listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }
    //为多个分类填充产品集合
    public void fill(List<Category> categories){
        for (Category category:categories){
            fill(category);
        }
    }
    //按照8个一行，拆分成多行填充产品集合,以便后续的显示
    public void fillByRow(List<Category> categories){
        int productNumberEachRow=8;
        for(Category category:categories){
            List<Product> products=category.getProducts();
            List<List<Product>> productsByRow =new ArrayList<>();
            for (int i=0;i<products.size();i+=productNumberEachRow){
                int size=i+productNumberEachRow;
                size=size>products.size()?products.size():size;
                List<Product> productsOfEachRow=products.subList(i,size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }
    //查询某个分类下的所有产品
    public List<Product> listByCategory(Category category){
        return productDAO.findByCategoryOrderById(category);
    }

    //设置销量和评论数量，用于前端页面展示
    public void setSaleAndReviewNumber(Product product){
        int saleCount=orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);//设置销量
        int reviewCount=reviewService.getCount(product);
        product.setReviewCount(reviewCount);//设置评论数量
    }
    public void setSaleAndReviewNumber(List<Product> products){
        for (Product product:products){
            setSaleAndReviewNumber(product);
        }
    }

    public List<Product> search(String keyword, int start, int size) {
        Sort sort=Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable=PageRequest.of(start,size,sort);
        List<Product> products=productDAO.findByNameLike("%"+keyword+"%",pageable);
        return  products;
    }


}
