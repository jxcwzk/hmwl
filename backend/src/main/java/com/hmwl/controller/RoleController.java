package com.hmwl.controller;

import com.hmwl.entity.Role;
import com.hmwl.service.RoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public List<Role> list() {
        return roleService.list();
    }

    @GetMapping("/page")
    public IPage<Role> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<Role> page = new Page<>(current, size);
        return roleService.page(page);
    }

    @GetMapping("/{id}")
    public Role getById(@PathVariable Long id) {
        return roleService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Role role) {
        return roleService.save(role);
    }

    @PutMapping
    public boolean update(@RequestBody Role role) {
        return roleService.updateById(role);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return roleService.removeById(id);
    }
}
