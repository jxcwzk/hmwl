package com.hmwl.service.impl;

import com.hmwl.entity.Route;
import com.hmwl.mapper.RouteMapper;
import com.hmwl.service.RouteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route> implements RouteService {
}
