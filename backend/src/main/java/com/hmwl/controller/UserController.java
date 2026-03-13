package com.hmwl.controller;

import com.hmwl.entity.User;
import com.hmwl.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 小程序微信登录
     * @param code 微信登录code
     * @return 登录结果
     */
    @PostMapping("/wxLogin")
    public Result wxLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        if (code == null || code.isEmpty()) {
            return Result.error("code不能为空");
        }

        try {
            User user = userService.wxLogin(code);
            if (user == null) {
                return Result.error("微信登录失败");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("userType", user.getUserType());
            data.put("status", user.getStatus());
            data.put("phone", user.getPhone());
            data.put("wechat", user.getWechat());
            data.put("remark", user.getRemark());
            data.put("businessUserId", user.getBusinessUserId());
            data.put("driverId", user.getDriverId());

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录异常: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo(@RequestParam Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("userType", user.getUserType());
        data.put("status", user.getStatus());
        data.put("phone", user.getPhone());
        data.put("wechat", user.getWechat());
        data.put("remark", user.getRemark());
        data.put("createTime", user.getCreateTime());

        return Result.success(data);
    }

    /**
     * 更新用户角色（管理员操作）
     * @param params 包含userId, userType, status
     * @return 更新结果
     */
    @PostMapping("/updateRole")
    public Result updateUserRole(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            Integer userType = Integer.valueOf(params.get("userType").toString());
            Integer status = Integer.valueOf(params.get("status").toString());

            boolean success = userService.updateUserRole(userId, userType, status);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新异常: " + e.getMessage());
        }
    }

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