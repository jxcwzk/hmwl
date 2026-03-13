package com.hmwl.controller;

import com.hmwl.entity.Menu;
import com.hmwl.service.MenuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public List<Menu> list() {
        return menuService.list();
    }

    @GetMapping("/page")
    public IPage<Menu> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<Menu> page = new Page<>(current, size);
        return menuService.page(page);
    }

    @GetMapping("/{id}")
    public Menu getById(@PathVariable Long id) {
        return menuService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Menu menu) {
        return menuService.save(menu);
    }

    @PutMapping
    public boolean update(@RequestBody Menu menu) {
        return menuService.updateById(menu);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return menuService.removeById(id);
    }
}
