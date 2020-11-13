package com.example.map_direction.morder;

import java.util.List;

public class LocationInformation {
    private double latitude;
    private double longitude;
    private String title;
    private List<String> waypoints;

    public LocationInformation(List<String> waypoints) {
        this.waypoints = waypoints;
    }

    public LocationInformation(double latitude, double longitude, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<String> waypoints) {
        this.waypoints = waypoints;
    }
}
