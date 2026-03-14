package com.example.kartransit;

public class Trip {
    private String routeId;
    private String serviceId;
    private String tripId;
    private String tripHeadsign;
    public Trip(String routeId, String serviceId, String tripId, String tripHeadsign) {
        this.routeId = routeId;
        this.serviceId = serviceId;
        this.tripId = tripId;
        this.tripHeadsign = tripHeadsign;
    }
    public String getRouteId() {
        return routeId;
    }
    public String getTripId() {
        return tripId;
    }
    public String getTripHeadsign() {
        return tripHeadsign;
    }
}