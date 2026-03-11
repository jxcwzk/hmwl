package com.hmwl.service.impl;

import com.hmwl.entity.Vehicle;
import com.hmwl.mapper.VehicleMapper;
import com.hmwl.service.VehicleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {
}
