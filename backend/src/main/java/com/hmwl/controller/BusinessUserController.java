package com.hmwl.controller;

import com.hmwl.entity.BusinessUser;
import com.hmwl.service.BusinessUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业用户控制器
 * 处理企业用户相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/business-user")
public class BusinessUserController {

    @Autowired
    private BusinessUserService businessUserService;

    /**
     * 获取所有企业用户列表
     * @return 企业用户列表
     */
    @GetMapping("/list")
    public List<BusinessUser> list() {
        return businessUserService.list();
    }

    /**
     * 分页获取企业用户列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的企业用户列表
     */
    @GetMapping("/page")
    public IPage<BusinessUser> page(@RequestParam(defaultValue = "1") Integer current, 
                                   @RequestParam(defaultValue = "10") Integer size) {
        Page<BusinessUser> page = new Page<>(current, size);
        return businessUserService.page(page);
    }

    /**
     * 根据ID获取企业用户详情
     * @param id 企业用户ID
     * @return 企业用户详情
     */
    @GetMapping("/{id}")
    public BusinessUser getById(@PathVariable Long id) {
        return businessUserService.getById(id);
    }

    /**
     * 保存企业用户信息
     * @param businessUser 企业用户信息
     * @return 保存是否成功
     */
    @PostMapping
    public boolean save(@RequestBody BusinessUser businessUser) {
        return businessUserService.save(businessUser);
    }

    /**
     * 更新企业用户信息
     * @param businessUser 企业用户信息
     * @return 更新是否成功
     */
    @PutMapping
    public boolean update(@RequestBody BusinessUser businessUser) {
        return businessUserService.updateById(businessUser);
    }

    /**
     * 删除企业用户
     * @param id 企业用户ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return businessUserService.removeById(id);
    }
}