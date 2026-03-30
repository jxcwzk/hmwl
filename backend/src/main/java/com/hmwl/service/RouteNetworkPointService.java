package com.hmwl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmwl.entity.RouteNetworkPoint;
import java.util.List;

public interface RouteNetworkPointService extends IService<RouteNetworkPoint> {
    List<Long> getNetworkPointIdsByRouteId(Long routeId);
    void addNetworkToRoute(Long routeId, Long networkPointId);
    void removeNetworkFromRoute(Long routeId, Long networkPointId);
}
