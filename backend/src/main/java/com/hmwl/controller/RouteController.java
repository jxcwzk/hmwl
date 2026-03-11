/**
 * 路线控制器，处理路线相关的HTTP请求
 * 
 * @author 系统生成
 * @date 2026-03-08
 */
package com.hmwl.controller;

import com.hmwl.entity.Route;
import com.hmwl.service.RouteService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    /**
     * 获取所有路线列表
     * 
     * @return 路线列表
     */
    @GetMapping("/list")
    public List<Route> list() {
        return routeService.list();
    }

    /**
     * 分页获取路线列表
     * 
     * @param current 当前页码，默认1
     * @param size 每页大小，默认10
     * @return 分页后的路线列表
     */
    @GetMapping("/page")
    public IPage<Route> page(
            @RequestParam(defaultValue = "1") Integer current, 
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Route> page = new Page<>(current, size);
        return routeService.page(page);
    }

    /**
     * 根据ID获取路线详情
     * 
     * @param id 路线ID
     * @return 路线详情
     */
    @GetMapping("/{id}")
    public Route getById(@PathVariable Long id) {
        return routeService.getById(id);
    }

    /**
     * 保存路线
     * 
     * @param route 路线对象
     * @return 是否保存成功
     */
    @PostMapping
    public boolean save(@RequestBody Route route) {
        return routeService.save(route);
    }

    /**
     * 更新路线
     * 
     * @param route 路线对象
     * @return 是否更新成功
     */
    @PutMapping
    public boolean update(@RequestBody Route route) {
        return routeService.updateById(route);
    }

    /**
     * 删除路线
     * 
     * @param id 路线ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return routeService.removeById(id);
    }
}
