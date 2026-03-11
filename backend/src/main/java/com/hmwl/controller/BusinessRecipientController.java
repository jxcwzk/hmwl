package com.hmwl.controller;

import com.hmwl.entity.BusinessRecipient;
import com.hmwl.service.BusinessRecipientService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业收件人控制器
 * 处理企业收件人相关的HTTP请求
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/business-recipient")
public class BusinessRecipientController {

    @Autowired
    private BusinessRecipientService businessRecipientService;

    /**
     * 获取所有企业收件人列表
     * @return 企业收件人列表
     */
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<BusinessRecipient> list() {
        return businessRecipientService.list();
    }

    /**
     * 根据企业用户ID获取企业收件人列表
     * @param businessUserId 企业用户ID
     * @return 企业收件人列表
     */
    @GetMapping(
        value = "/listByBusinessUserId/{businessUserId}", 
        produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public List<BusinessRecipient> listByBusinessUserId(@PathVariable Long businessUserId) {
        return businessRecipientService.listByBusinessUserId(businessUserId);
    }

    /**
     * 分页获取企业收件人列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的企业收件人列表
     */
    @GetMapping(
        value = "/page", 
        produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public IPage<BusinessRecipient> page(@RequestParam(defaultValue = "1") Integer current, 
                                         @RequestParam(defaultValue = "10") Integer size) {
        Page<BusinessRecipient> page = new Page<>(current, size);
        return businessRecipientService.page(page);
    }

    /**
     * 根据ID获取企业收件人详情
     * @param id 企业收件人ID
     * @return 企业收件人详情
     */
    @GetMapping(
        value = "/{id}", 
        produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public BusinessRecipient getById(@PathVariable Long id) {
        return businessRecipientService.getById(id);
    }

    /**
     * 保存企业收件人信息
     * @param businessRecipient 企业收件人信息
     * @return 保存是否成功
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public boolean save(@RequestBody BusinessRecipient businessRecipient) {
        return businessRecipientService.save(businessRecipient);
    }

    /**
     * 更新企业收件人信息
     * @param businessRecipient 企业收件人信息
     * @return 更新是否成功
     */
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public boolean update(@RequestBody BusinessRecipient businessRecipient) {
        return businessRecipientService.updateById(businessRecipient);
    }

    /**
     * 删除企业收件人
     * @param id 企业收件人ID
     * @return 删除是否成功
     */
    @DeleteMapping(
        value = "/{id}", 
        produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public boolean delete(@PathVariable Long id) {
        return businessRecipientService.removeById(id);
    }
}