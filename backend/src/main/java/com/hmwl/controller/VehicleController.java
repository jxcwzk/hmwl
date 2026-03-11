package com.hmwl.controller;

import com.hmwl.entity.Vehicle;
import com.hmwl.service.VehicleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆控制器
 * 处理车辆相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * 获取所有车辆列表
     * @return 车辆列表
     */
    @GetMapping("/list")
    public List<Vehicle> list() {
        return vehicleService.list();
    }

    /**
     * 分页获取车辆列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的车辆列表
     */
    @GetMapping("/page")
    public IPage<Vehicle> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<Vehicle> page = new Page<>(current, size);
        return vehicleService.page(page);
    }

    /**
     * 根据ID获取车辆详情
     * @param id 车辆ID
     * @return 车辆详情
     */
    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable Long id) {
        return vehicleService.getById(id);
    }

    /**
     * 保存车辆信息
     * @param vehicle 车辆信息
     * @return 保存是否成功
     */
    @PostMapping
    public boolean save(@RequestBody Vehicle vehicle) {
        return vehicleService.save(vehicle);
    }

    /**
     * 更新车辆信息
     * @param vehicle 车辆信息
     * @return 更新是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Vehicle vehicle) {
        return vehicleService.updateById(vehicle);
    }

    /**
     * 删除车辆
     * @param id 车辆ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return vehicleService.removeById(id);
    }
}
