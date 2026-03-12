package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmwl.entity.NetworkPoint;
import com.hmwl.mapper.NetworkPointMapper;
import com.hmwl.service.DistanceCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DistanceCalculatorServiceImpl implements DistanceCalculatorService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Autowired(required = false)
    private NetworkPointMapper networkPointMapper;

    @Override
    public double calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        if (startLat == 0 && startLng == 0 || endLat == 0 && endLng == 0) {
            return 0;
        }
        return haversineDistance(startLat, startLng, endLat, endLng);
    }

    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(EARTH_RADIUS_KM * c * 100.0) / 100.0;
    }

    @Override
    public double[] getCoordinatesByAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }
        return parseCoordinatesFromAddress(address);
    }

    @Override
    public double[] getCoordinatesByNetworkPointId(Long networkPointId) {
        if (networkPointId == null) {
            return null;
        }

        NetworkPoint networkPoint = networkPointMapper.selectById(networkPointId);
        if (networkPoint == null || networkPoint.getAddress() == null) {
            return null;
        }

        return parseCoordinatesFromAddress(networkPoint.getAddress());
    }

    private double[] parseCoordinatesFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }

        double[] coords = new double[2];

        String[] parts = address.split(",");
        if (parts.length == 2) {
            try {
                double lat = Double.parseDouble(parts[0].trim());
                double lng = Double.parseDouble(parts[1].trim());
                if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                    coords[0] = lat;
                    coords[1] = lng;
                    return coords;
                }
            } catch (NumberFormatException e) {
            }
        }

        Pattern latPattern = Pattern.compile("(-?\\d+\\.\\d+)[°]?\\s*[NnSs]?");
        Pattern lngPattern = Pattern.compile("(-?\\d+\\.\\d+)[°]?\\s*[EeWw]?");

        Matcher latMatcher = latPattern.matcher(address);
        Matcher lngMatcher = lngPattern.matcher(address);

        if (latMatcher.find() && lngMatcher.find()) {
            try {
                coords[0] = Double.parseDouble(latMatcher.group(1));
                coords[1] = Double.parseDouble(lngMatcher.group(1));

                if (address.contains("S")) {
                    coords[0] = -coords[0];
                }
                if (address.contains("W")) {
                    coords[1] = -coords[1];
                }

                return coords;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
