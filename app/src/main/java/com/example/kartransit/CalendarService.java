package com.example.kartransit;
public class CalendarService {
    private String serviceId;
    private int monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    public CalendarService(String serviceId, int monday, int tuesday, int wednesday, int thursday, int friday, int saturday, int sunday) {
        this.serviceId = serviceId;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }
    public String getServiceId() {
        return serviceId;
    }
    public String getDayStatus(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return (sunday == 1) ? "Çalışıyor" : "Çalışmıyor";
            case 2: return (monday == 1) ? "Çalışıyor" : "Çalışmıyor";
            case 3: return (tuesday == 1) ? "Çalışıyor" : "Çalışmıyor";
            case 4: return (wednesday == 1) ? "Çalışıyor" : "Çalışmıyor";
            case 5: return (thursday == 1) ? "Çalışıyor" : "Çalışmıyor";
            case 6: return (friday == 1) ? "Çalışıyor" : "Çalışmıyor";
            case 7: return (saturday == 1) ? "Çalışıyor" : "Çalışmıyor";
            default: return "Bilinmiyor";
        }
    }
}