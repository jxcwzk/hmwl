package com.hmwl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmwl.entity.Route;
import java.util.List;

public interface RouteService extends IService<Route> {
    Route findByDestination(String destinationCity);
    List<Route> findRoutesByDestination(String destinationCity);
}
