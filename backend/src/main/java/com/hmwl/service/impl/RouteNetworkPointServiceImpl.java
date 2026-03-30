package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.RouteNetworkPoint;
import com.hmwl.mapper.RouteNetworkPointMapper;
import com.hmwl.service.RouteNetworkPointService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteNetworkPointServiceImpl extends ServiceImpl<RouteNetworkPointMapper, RouteNetworkPoint> implements RouteNetworkPointService {

    @Override
    public List<Long> getNetworkPointIdsByRouteId(Long routeId) {
        LambdaQueryWrapper<RouteNetworkPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RouteNetworkPoint::getRouteId, routeId);
        List<RouteNetworkPoint> list = this.list(wrapper);
        return list.stream()
                .map(RouteNetworkPoint::getNetworkPointId)
                .collect(Collectors.toList());
    }

    @Override
    public void addNetworkToRoute(Long routeId, Long networkPointId) {
        RouteNetworkPoint rnp = new RouteNetworkPoint();
        rnp.setRouteId(routeId);
        rnp.setNetworkPointId(networkPointId);
        this.save(rnp);
    }

    @Override
    public void removeNetworkFromRoute(Long routeId, Long networkPointId) {
        LambdaQueryWrapper<RouteNetworkPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RouteNetworkPoint::getRouteId, routeId)
               .eq(RouteNetworkPoint::getNetworkPointId, networkPointId);
        this.remove(wrapper);
    }
}
