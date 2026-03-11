package com.hmwl.controller;

import com.hmwl.entity.Customer;
import com.hmwl.service.CustomerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户控制器
 * 处理客户相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 获取所有客户列表
     * @return 客户列表
     */
    @GetMapping("/list")
    public List<Customer> list() {
        return customerService.list();
    }

    /**
     * 分页获取客户列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的客户列表
     */
    @GetMapping("/page")
    public IPage<Customer> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<Customer> page = new Page<>(current, size);
        return customerService.page(page);
    }

    /**
     * 根据ID获取客户详情
     * @param id 客户ID
     * @return 客户详情
     */
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    /**
     * 保存客户信息
     * @param customer 客户信息
     * @return 保存是否成功
     */
    @PostMapping
    public boolean save(@RequestBody Customer customer) {
        return customerService.save(customer);
    }

    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return 更新是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Customer customer) {
        return customerService.updateById(customer);
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return customerService.removeById(id);
    }
}