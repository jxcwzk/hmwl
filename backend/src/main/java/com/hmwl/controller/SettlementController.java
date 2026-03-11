package com.hmwl.controller;

import com.hmwl.entity.Settlement;
import com.hmwl.service.SettlementService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 结算控制器
 * 处理结算相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/settlement")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    /**
     * 获取所有结算列表
     * @return 结算列表
     */
    @GetMapping("/list")
    public List<Settlement> list() {
        return settlementService.list();
    }

    /**
     * 分页获取结算列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的结算列表
     */
    @GetMapping("/page")
    public IPage<Settlement> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<Settlement> page = new Page<>(current, size);
        return settlementService.page(page);
    }

    /**
     * 根据ID获取结算详情
     * @param id 结算ID
     * @return 结算详情
     */
    @GetMapping("/{id}")
    public Settlement getById(@PathVariable Long id) {
        return settlementService.getById(id);
    }

    /**
     * 保存结算信息
     * @param settlement 结算信息
     * @return 保存是否成功
     */
    @PostMapping
    public boolean save(@RequestBody Settlement settlement) {
        return settlementService.save(settlement);
    }

    /**
     * 更新结算信息
     * @param settlement 结算信息
     * @return 更新是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Settlement settlement) {
        return settlementService.updateById(settlement);
    }

    /**
     * 删除结算
     * @param id 结算ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return settlementService.removeById(id);
    }

    /**
     * 对账管理
     * @return 对账结果
     */
    @GetMapping("/reconciliation")
    public Object reconciliation() {
        // 对账管理逻辑
        return "对账管理";
    }

    /**
     * 统计分析
     * @return 统计结果
     */
    @GetMapping("/statistics")
    public Object statistics() {
        // 统计分析逻辑
        return "统计分析";
    }
}
