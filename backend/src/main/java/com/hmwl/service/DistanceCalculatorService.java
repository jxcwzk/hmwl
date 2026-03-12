package com.hmwl.service;

public interface DistanceCalculatorService {

    /**
     * 根据两点坐标计算距离
     * @param startLat 起点纬度
     * @param startLng 起点经度
     * @param endLat 终点纬度
     * @param endLng 终点经度
     * @return 距离（公里）
     */
    double calculateDistance(double startLat, double startLng, double endLat, double endLng);

    /**
     * 根据地址获取坐标（需要地图API支持）
     * @param address 地址
     * @return 坐标数组 [lat, lng]，如果获取失败返回null
     */
    double[] getCoordinatesByAddress(String address);

    /**
     * 根据网点ID获取坐标
     * @param networkPointId 网点ID
     * @return 坐标数组 [lat, lng]
     */
    double[] getCoordinatesByNetworkPointId(Long networkPointId);
}
