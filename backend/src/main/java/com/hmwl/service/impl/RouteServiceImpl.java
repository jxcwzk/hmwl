package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.NetworkPoint;
import com.hmwl.entity.Route;
import com.hmwl.entity.RouteNetworkPoint;
import com.hmwl.mapper.NetworkPointMapper;
import com.hmwl.mapper.RouteMapper;
import com.hmwl.mapper.RouteNetworkPointMapper;
import com.hmwl.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route> implements RouteService {

    @Autowired
    private RouteNetworkPointMapper routeNetworkPointMapper;

    @Autowired
    private NetworkPointMapper networkPointMapper;

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

    @Override
    public List<NetworkPoint> getNetworkPointsByRouteId(Long routeId) {
        return routeNetworkPointMapper.selectNetworkPointsByRouteId(routeId);
    }

    @Override
    public List<Route> matchRoutesByCities(String startCity, String destinationCity) {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Route::getStartCity, startCity);
        wrapper.like(Route::getDestinationCity, destinationCity);
        List<Route> routes = this.list(wrapper);

        for (Route route : routes) {
            List<NetworkPoint> networkPoints = getNetworkPointsByRouteId(route.getId());
            route.setNetworkPoints(networkPoints);
        }
        return routes;
    }
}
