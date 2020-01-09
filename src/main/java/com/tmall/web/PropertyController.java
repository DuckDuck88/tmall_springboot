package com.tmall.web;

import com.tmall.pojo.Property;
import com.tmall.service.PropertyService;
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

@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    @GetMapping(value = "/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid,
                                         @RequestParam(value = "start",defaultValue = "0") int start,
                                         @RequestParam(value = "size",defaultValue = "5")int size){
        start=start>0?start:0;
        Page4Navigator<Property> page4Navigator=propertyService.list(cid, start, size, 5);
        return page4Navigator;
    }

    @GetMapping(value = "/properties/{id}")
    public Property get(@PathVariable("id") int id) throws Exception{
        Property bean=propertyService.get(id);
        return bean;
    }

    @PostMapping(value = "properties")
    public Object add(@RequestBody Property bean) throws  Exception{
        propertyService.add(bean);
        return bean;
    }

    @DeleteMapping(value = "properties/{id}")
    public String delete(@PathVariable("id") int id){
        propertyService.delete(id);
        return null;
    }

    @PutMapping(value = "properties")
    public Object update(@RequestBody Property bean) throws Exception{
        propertyService.update(bean);
        return bean;
    }

}
