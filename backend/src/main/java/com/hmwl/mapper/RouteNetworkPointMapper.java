package com.hmwl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmwl.entity.RouteNetworkPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RouteNetworkPointMapper extends BaseMapper<RouteNetworkPoint> {

    @Select("SELECT np.* FROM network_point np " +
            "INNER JOIN route_network_point rnp ON np.id = rnp.network_point_id " +
            "WHERE rnp.route_id = #{routeId} ORDER BY rnp.sequence")
    List<com.hmwl.entity.NetworkPoint> selectNetworkPointsByRouteId(@Param("routeId") Long routeId);
}
