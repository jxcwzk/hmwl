package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.Route;
import com.hmwl.mapper.RouteMapper;
import com.hmwl.service.RouteService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route> implements RouteService {
    
    @Override
    public Route findByDestination(String destinationCity) {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Route::getDestinationCity, destinationCity);
        wrapper.last("LIMIT 1");
        return this.getOne(wrapper);
    }
    
    @Override
    public List<Route> findRoutesByDestination(String destinationCity) {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Route::getDestinationCity, destinationCity);
        return this.list(wrapper);
    }
}
