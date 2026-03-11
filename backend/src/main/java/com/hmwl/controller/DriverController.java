package com.hmwl.controller;

import com.hmwl.entity.Driver;
import com.hmwl.service.DriverService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 司机控制器
 * 处理司机相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    /**
     * 获取所有司机列表
     * @return 司机列表
     */
    @GetMapping("/list")
    public List<Driver> list() {
        return driverService.list();
    }

    /**
     * 分页获取司机列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的司机列表
     */
    @GetMapping("/page")
    public IPage<Driver> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<Driver> page = new Page<>(current, size);
        return driverService.page(page);
    }

    /**
     * 根据ID获取司机详情
     * @param id 司机ID
     * @return 司机详情
     */
    @GetMapping("/{id}")
    public Driver getById(@PathVariable Long id) {
        return driverService.getById(id);
    }

    /**
     * 保存司机信息
     * @param driver 司机信息
     * @return 保存是否成功
     */
    @PostMapping
    public boolean save(@RequestBody Driver driver) {
        return driverService.save(driver);
    }

    /**
     * 更新司机信息
     * @param driver 司机信息
     * @return 更新是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Driver driver) {
        return driverService.updateById(driver);
    }

    /**
     * 删除司机
     * @param id 司机ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return driverService.removeById(id);
    }
}
