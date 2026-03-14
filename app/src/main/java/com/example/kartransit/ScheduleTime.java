package com.example.kartransit;

/**
 * Bir sefere ait güzergah bilgisini ve kalkış saatini tutuyo.
 * headsign: Seferin yönü (Örn: GÖLBAŞI-ULUS)
 * departureTime: Kalkış saati (Örn: 08:00:00)
 */
public class ScheduleTime {

    private String headsign;

    private String departureTime;

    public ScheduleTime(String headsign, String departureTime) {
        this.headsign = headsign;
        this.departureTime = departureTime;
    }

    public String getHeadsign() {
        return headsign;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    @Override
    public String toString() {
        return headsign + " - " + departureTime;
    }
}