package com.hmwl.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmwl.entity.Invoice;
import com.hmwl.entity.Settlement;
import com.hmwl.service.InvoiceService;
import com.hmwl.service.SettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SettlementService settlementService;

    @GetMapping("/list")
    public List<Invoice> list() {
        return invoiceService.list();
    }

    @GetMapping("/page")
    public IPage<Invoice> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<Invoice> page = new Page<>(current, size);
        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();

        if (customerId != null) {
            queryWrapper.eq("customer_id", customerId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge("invoice_date", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le("invoice_date", endDate);
        }

        queryWrapper.orderByDesc("create_time");
        return invoiceService.page(page, queryWrapper);
    }

    @GetMapping("/{id}")
    public Invoice getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Invoice invoice) {
        return invoiceService.save(invoice);
    }

    @PutMapping
    public boolean update(@RequestBody Invoice invoice) {
        return invoiceService.updateById(invoice);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return invoiceService.removeById(id);
    }

    @PostMapping("/create/{settlementId}")
    public boolean createInvoice(@PathVariable Long settlementId) {
        Settlement settlement = settlementService.getById(settlementId);
        if (settlement == null) {
            log.error("结算记录不存在: {}", settlementId);
            return false;
        }

        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("settlement_id", settlementId);
        Long existingCount = invoiceService.count(queryWrapper);
        if (existingCount > 0) {
            log.error("该结算记录已开票: {}", settlementId);
            return false;
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(generateInvoiceNo());
        invoice.setSettlementId(settlementId);
        invoice.setOrderId(settlement.getOrderId());
        invoice.setCustomerId(settlement.getCustomerId());
        invoice.setAmount(settlement.getAmount());
        invoice.setStatus(1);
        invoice.setInvoiceDate(new Date());
        invoice.setCreateTime(new Date());
        invoice.setUpdateTime(new Date());

        return invoiceService.save(invoice);
    }

    private String generateInvoiceNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "INV" + sdf.format(new Date()) + (int) (Math.random() * 1000);
    }
}
