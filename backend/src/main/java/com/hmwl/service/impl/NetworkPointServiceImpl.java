package com.hmwl.service.impl;

import com.hmwl.entity.NetworkPoint;
import com.hmwl.mapper.NetworkPointMapper;
import com.hmwl.service.NetworkPointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NetworkPointServiceImpl extends ServiceImpl<NetworkPointMapper, NetworkPoint> implements NetworkPointService {
}
