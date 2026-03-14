package com.example.kartransit;

public class Stop {
    private String stopId;
    private String stopName;
    private String stopCode;
    private double stopLat;
    private double stopLon;
    private String departureTime;
    private String liveDepartureTime;
    private String cityName; // Hangi şehre ait olduğunu tutmak için eklendi

    public Stop() {}

    public Stop(String stopId, String stopName, double stopLat, double stopLon) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
    }

    // Şehir ismiyle beraber oluşturmak için yapıcı metod
    public Stop(String stopId, String stopName, double stopLat, double stopLon, String cityName) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.cityName = cityName;
    }

    public String getStopId() { return stopId; }
    public void setStopId(String stopId) { this.stopId = stopId; }

    public String getStopName() { return stopName; }
    public void setStopName(String stopName) { this.stopName = stopName; }

    public String getStopCode() { return stopCode; }
    public void setStopCode(String stopCode) { this.stopCode = stopCode; }

    public double getStopLat() { return stopLat; }
    public void setStopLat(double stopLat) { this.stopLat = stopLat; }

    public double getStopLon() { return stopLon; }
    public void setStopLon(double stopLon) { this.stopLon = stopLon; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getLiveDepartureTime() { return liveDepartureTime; }
    public void setLiveDepartureTime(String time) { this.liveDepartureTime = time; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    @Override
    public String toString() {
        String displayCode = (stopCode != null && !stopCode.isEmpty()) ? stopCode : stopId;
        return stopName + " (" + displayCode + ")";
    }
}
