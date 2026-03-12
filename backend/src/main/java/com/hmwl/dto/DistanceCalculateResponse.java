package com.hmwl.dto;

public class DistanceCalculateResponse {
    private Double distance;
    private String message;
    private Boolean success;

    public DistanceCalculateResponse() {
    }

    public DistanceCalculateResponse(Double distance, String message, Boolean success) {
        this.distance = distance;
        this.message = message;
        this.success = success;
    }

    public static DistanceCalculateResponse success(Double distance) {
        return new DistanceCalculateResponse(distance, "距离计算成功", true);
    }

    public static DistanceCalculateResponse fail(String message) {
        return new DistanceCalculateResponse(null, message, false);
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
