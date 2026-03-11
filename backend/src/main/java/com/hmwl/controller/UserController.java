package com.hmwl.controller;

import com.hmwl.entity.User;
import com.hmwl.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    /**
     * 分页获取用户列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的用户列表
     */
    @GetMapping("/page")
    public IPage<User> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<User> page = new Page<>(current, size);
        return userService.page(page);
    }

    /**
     * 根据ID获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * 保存用户信息
     * @param user 用户信息
     * @return 保存是否成功
     */
    @PostMapping
    public boolean save(@RequestBody User user) {
        return userService.save(user);
    }

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新是否成功
     */
    @PutMapping
    public boolean update(@RequestBody User user) {
        return userService.updateById(user);
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return userService.removeById(id);
    }
    

}