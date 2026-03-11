package com.hmwl.controller;

import com.hmwl.entity.NetworkPoint;
import com.hmwl.service.NetworkPointService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 网点控制器
 * 处理网点相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/network-point")
public class NetworkPointController {

    @Autowired
    private NetworkPointService networkPointService;

    /**
     * 获取所有网点列表
     * @return 网点列表
     */
    @GetMapping("/list")
    public List<NetworkPoint> list() {
        return networkPointService.list();
    }

    /**
     * 分页获取网点列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的网点列表
     */
    @GetMapping("/page")
    public IPage<NetworkPoint> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<NetworkPoint> page = new Page<>(current, size);
        return networkPointService.page(page);
    }

    /**
     * 根据ID获取网点详情
     * @param id 网点ID
     * @return 网点详情
     */
    @GetMapping("/{id}")
    public NetworkPoint getById(@PathVariable Long id) {
        return networkPointService.getById(id);
    }

    /**
     * 保存网点信息
     * @param networkPoint 网点信息
     * @return 保存是否成功
     */
    @PostMapping
    public boolean save(@RequestBody NetworkPoint networkPoint) {
        return networkPointService.save(networkPoint);
    }

    /**
     * 更新网点信息
     * @param networkPoint 网点信息
     * @return 更新是否成功
     */
    @PutMapping
    public boolean update(@RequestBody NetworkPoint networkPoint) {
        return networkPointService.updateById(networkPoint);
    }

    /**
     * 删除网点
     * @param id 网点ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return networkPointService.removeById(id);
    }
}
