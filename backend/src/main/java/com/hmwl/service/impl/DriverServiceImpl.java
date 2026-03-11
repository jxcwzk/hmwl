package com.hmwl.service.impl;

import com.hmwl.entity.Driver;
import com.hmwl.mapper.DriverMapper;
import com.hmwl.service.DriverService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DriverServiceImpl extends ServiceImpl<DriverMapper, Driver> implements DriverService {
}
