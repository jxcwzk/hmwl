package com.hmwl.controller;

import com.hmwl.entity.BusinessCustomer;
import com.hmwl.service.BusinessCustomerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业客户控制器
 * 处理企业客户相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/business-customer")
public class BusinessCustomerController {

    @Autowired
    private BusinessCustomerService businessCustomerService;

    /**
     * 获取所有企业客户列表
     * @return 企业客户列表
     */
    @GetMapping("/list")
    public List<BusinessCustomer> list(
            @RequestParam(required = false) Long businessUserId,
            @RequestParam(required = false, defaultValue = "-1") Integer type) {
        if (businessUserId != null && businessUserId > 0) {
            if (type != null && type >= 0) {
                return businessCustomerService.listByBusinessUserIdAndType(businessUserId, type);
            }
            return businessCustomerService.listByBusinessUserId(businessUserId);
        }
        return new ArrayList<>();
    }

    /**
     * 分页获取企业客户列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的企业客户列表
     */
    @GetMapping("/page")
    public IPage<BusinessCustomer> page(@RequestParam(defaultValue = "1") Integer current, 
                                       @RequestParam(defaultValue = "10") Integer size) {
        Page<BusinessCustomer> page = new Page<>(current, size);
        return businessCustomerService.page(page);
    }

    /**
     * 根据ID获取企业客户详情
     * @param id 企业客户ID
     * @return 企业客户详情
     */
    @GetMapping("/{id}")
    public BusinessCustomer getById(@PathVariable Long id) {
        return businessCustomerService.getById(id);
    }

    /**
     * 保存企业客户信息
     * @param businessCustomer 企业客户信息
     * @return 保存是否成功
     */
    @PostMapping
    public boolean save(@RequestBody BusinessCustomer businessCustomer) {
        return businessCustomerService.save(businessCustomer);
    }

    /**
     * 更新企业客户信息
     * @param businessCustomer 企业客户信息
     * @return 更新是否成功
     */
    @PutMapping
    public boolean update(@RequestBody BusinessCustomer businessCustomer) {
        return businessCustomerService.updateById(businessCustomer);
    }

    /**
     * 删除企业客户
     * @param id 企业客户ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return businessCustomerService.removeById(id);
    }
    
    /**
     * 根据企业用户ID获取企业客户列表
     * @param businessUserId 企业用户ID
     * @return 企业客户列表
     */
    @GetMapping("/listByBusinessUserId/{businessUserId}")
    public List<BusinessCustomer> listByBusinessUserId(@PathVariable Long businessUserId) {
        return businessCustomerService.listByBusinessUserId(businessUserId);
    }
    
    /**
     * 根据企业用户ID和类型获取企业客户列表
     * @param businessUserId 企业用户ID
     * @param type 类型，0表示发件人信息，1表示收件人信息
     * @return 企业客户列表
     */
    @GetMapping("/listByBusinessUserIdAndType/{businessUserId}/{type}")
    public List<BusinessCustomer> listByBusinessUserIdAndType(@PathVariable Long businessUserId, @PathVariable Integer type) {
        return businessCustomerService.listByBusinessUserIdAndType(businessUserId, type);
    }
}