package com.hmwl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.Invoice;
import com.hmwl.mapper.InvoiceMapper;
import com.hmwl.service.InvoiceService;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl extends ServiceImpl<InvoiceMapper, Invoice> implements InvoiceService {
}
