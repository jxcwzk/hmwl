package com.hmwl.service.impl;

import com.hmwl.entity.Customer;
import com.hmwl.mapper.CustomerMapper;
import com.hmwl.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
}