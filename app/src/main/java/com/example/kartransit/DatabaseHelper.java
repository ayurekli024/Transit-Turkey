package com.example.kartransit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String DATABASE_NAME;
    private static final int DATABASE_VERSION = 4;
    private final String DATABASE_PATH;
    private final Context context;
    private final String city;

    public DatabaseHelper(Context context, String city) {
        super(context, getDatabaseNameForCity(city), null, DATABASE_VERSION);
        this.context = context;
        this.city = (city != null) ? city.toLowerCase() : "ankara";
        this.DATABASE_NAME = getDatabaseNameForCity(this.city);
        this.DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getParent() + "/";

        try {
            createDatabase();
        } catch (IOException e) {
            Log.e("DB_ERROR", "Veritabanı oluşturulamadı: " + e.getMessage());
        }
    }

    private static String getDatabaseNameForCity(String city) {
        if (city == null) return "transit_data.db";
        switch (city.toLowerCase()) {
            case "istanbul": return "transit_data_ist.db";
            case "izmir": return "transit_data_izm.db";
            default: return "transit_data.db";
        }
    }

    private void createDatabase() throws IOException {
        if (!checkDatabase()) {
            this.getReadableDatabase();
            this.close();
            copyDatabase();
        } else {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
            if (db.getVersion() < DATABASE_VERSION) {
                db.close();
                copyDatabase();
            } else {
                db.close();
            }
        }
    }

    private boolean checkDatabase() {
        return new File(DATABASE_PATH + DATABASE_NAME).exists();
    }

    private void copyDatabase() throws IOException {
        try (InputStream input = context.getAssets().open(DATABASE_NAME);
             OutputStream output = new FileOutputStream(DATABASE_PATH + DATABASE_NAME)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            db.setVersion(DATABASE_VERSION);
            db.close();
            Log.d("DB_STATUS", "Veritabanı başarıyla kopyalandı/güncellendi: " + DATABASE_NAME);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites (stop_id TEXT PRIMARY KEY, stop_name TEXT, stop_lat REAL, stop_lon REAL, city_name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            try { 
                copyDatabase(); 
            } catch (IOException e) { 
                Log.e("DB_UPGRADE", "Veritabanı kopyalanamadı", e);
            }
        }
        // favorites tablosu kontrolü
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites (stop_id TEXT PRIMARY KEY, stop_name TEXT, stop_lat REAL, stop_lon REAL, city_name TEXT)");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites (stop_id TEXT PRIMARY KEY, stop_name TEXT, stop_lat REAL, stop_lon REAL, city_name TEXT)");
    }

    public void addFavorite(Stop stop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stop_id", stop.getStopId());
        values.put("stop_name", stop.getStopName());
        values.put("stop_lat", stop.getStopLat());
        values.put("stop_lon", stop.getStopLon());
        values.put("city_name", this.city);
        db.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeFavorite(String stopId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favorites", "stop_id = ?", new String[]{stopId});
    }

    public boolean isFavorite(String stopId) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;
        try (Cursor cursor = db.rawQuery("SELECT 1 FROM favorites WHERE stop_id = ?", new String[]{stopId})) {
            exists = cursor.getCount() > 0;
        }
        return exists;
    }

    public List<Stop> getAllFavorites() {
        List<Stop> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT stop_id, stop_name, stop_lat, stop_lon, city_name FROM favorites", null)) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(new Stop(cursor.getString(0), cursor.getString(1), 
                             cursor.getDouble(2), cursor.getDouble(3), cursor.getString(4)));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public List<Stop> getNearbyStops(double userLat, double userLon, double radiusInMeters) {
        List<Stop> stopList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Yaklaşık derece hesabı (250m için ~0.00225 derece)
        double latDelta = radiusInMeters / 111320.0;
        double lonDelta = radiusInMeters / (111320.0 * Math.cos(Math.toRadians(userLat)));

        String sql = "SELECT stop_id, stop_name, stop_lat, stop_lon FROM stops " +
                "WHERE stop_lat BETWEEN ? AND ? AND stop_lon BETWEEN ? AND ? LIMIT 100";
        
        String[] args = {
                String.valueOf(userLat - latDelta),
                String.valueOf(userLat + latDelta),
                String.valueOf(userLon - lonDelta),
                String.valueOf(userLon + lonDelta)
        };

        try (Cursor cursor = db.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                do {
                    stopList.add(new Stop(cursor.getString(0), cursor.getString(1), 
                             cursor.getDouble(2), cursor.getDouble(3), this.city));
                } while (cursor.moveToNext());
            }
        }
        return stopList;
    }

    public List<Stop> searchStops(String query) {
        List<Stop> stopList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isIstanbul = "istanbul".equals(this.city);

        String sql;
        if (isIstanbul) {
            sql = "SELECT stop_id, stop_name, stop_lat, stop_lon, stop_code FROM stops " +
                    "WHERE stop_name LIKE ? OR stop_code LIKE ? LIMIT 50";
        } else {
            sql = "SELECT stop_id, stop_name, stop_lat, stop_lon, NULL as stop_code FROM stops " +
                    "WHERE stop_name LIKE ? OR stop_id LIKE ? LIMIT 50";
        }

        try (Cursor cursor = db.rawQuery(sql, new String[]{"%" + query + "%", query + "%"})) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    if (isIstanbul) {
                        String code = cursor.getString(4);
                        if (code != null && !code.isEmpty()) id = code;
                    }
                    stopList.add(new Stop(id, cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3), this.city));
                } while (cursor.moveToNext());
            }
        }
        return stopList;
    }

    public List<Route> getRoutesByStopId(String stopId) {
        List<Route> routeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isIstanbul = "istanbul".equals(this.city);

        String sql;
        String[] args;
        if (isIstanbul) {
            sql = "SELECT DISTINCT r.route_id, r.route_short_name, r.route_long_name " +
                    "FROM routes r JOIN trips t ON r.route_id = t.route_id " +
                    "JOIN stop_times st ON t.trip_id = st.trip_id " +
                    "WHERE st.stop_id = ? OR st.stop_id IN (SELECT stop_id FROM stops WHERE stop_code = ?) " +
                    "ORDER BY CASE WHEN r.route_short_name GLOB '[0-9]*' THEN CAST(r.route_short_name AS INTEGER) ELSE 99999 END, r.route_short_name";
            args = new String[]{stopId, stopId};
        } else {
            sql = "SELECT DISTINCT r.route_id, r.route_short_name, r.route_long_name " +
                    "FROM routes r JOIN trips t ON r.route_id = t.route_id " +
                    "JOIN stop_times st ON t.trip_id = st.trip_id " +
                    "WHERE st.stop_id = ? " +
                    "ORDER BY CASE WHEN r.route_short_name GLOB '[0-9]*' THEN CAST(r.route_short_name AS INTEGER) ELSE 99999 END, r.route_short_name";
            args = new String[]{stopId};
        }

        try (Cursor cursor = db.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndex("route_id");
                int shortIdx = cursor.getColumnIndex("route_short_name");
                int longIdx = cursor.getColumnIndex("route_long_name");
                do {
                    routeList.add(new Route(cursor.getString(idIdx), cursor.getString(shortIdx), cursor.getString(longIdx)));
                } while (cursor.moveToNext());
            }
        }
        return routeList;
    }

    public List<Stop> getStopsByRouteId(String routeId, String selectedStopId) {
        List<Stop> stopList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isIstanbul = "istanbul".equals(this.city);
        String currentTime = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
        String dayColumn = getDayColumn();

        String sql;
        String[] args;

        if (isIstanbul) {
            sql = "SELECT s.stop_id, s.stop_name, s.stop_lat, s.stop_lon, st.departure_time " +
                    "FROM stops s JOIN stop_times st ON s.stop_id = st.stop_id OR s.stop_code = st.stop_id " +
                    "JOIN trips t ON st.trip_id = t.trip_id JOIN calendar c ON t.service_id = c.service_id " +
                    "WHERE t.trip_id = (SELECT st2.trip_id FROM stop_times st2 JOIN trips t2 ON st2.trip_id = t2.trip_id " +
                    "JOIN calendar c2 ON t2.service_id = c2.service_id WHERE t2.route_id = ? " +
                    "AND (st2.stop_id = ? OR st2.stop_id IN (SELECT stop_id FROM stops WHERE stop_code = ?)) " +
                    "AND st2.departure_time >= ? AND c2." + dayColumn + " = 1 ORDER BY st2.departure_time ASC LIMIT 1) " +
                    "GROUP BY s.stop_id ORDER BY st.stop_sequence ASC";
            args = new String[]{routeId, selectedStopId, selectedStopId, currentTime};
        } else {
            sql = "SELECT s.stop_id, s.stop_name, s.stop_lat, s.stop_lon, st.departure_time " +
                    "FROM stops s JOIN stop_times st ON s.stop_id = st.stop_id " +
                    "JOIN trips t ON st.trip_id = t.trip_id JOIN calendar c ON t.service_id = c.service_id " +
                    "WHERE t.trip_id = (SELECT st2.trip_id FROM stop_times st2 JOIN trips t2 ON st2.trip_id = t2.trip_id " +
                    "JOIN calendar c2 ON t2.service_id = c2.service_id WHERE t2.route_id = ? AND st2.stop_id = ? " +
                    "AND st2.departure_time >= ? AND c2." + dayColumn + " = 1 ORDER BY st2.departure_time ASC LIMIT 1) " +
                    "ORDER BY st.stop_sequence ASC";
            args = new String[]{routeId, selectedStopId, currentTime};
        }

        try (Cursor cursor = db.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                do {
                    Stop stop = new Stop(cursor.getString(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3), this.city);
                    stop.setDepartureTime(cursor.getString(4));
                    stopList.add(stop);
                } while (cursor.moveToNext());
            }
        }
        return stopList;
    }

    public List<ScheduleTime> getSchedule(String routeId, String stopId, int dayOfWeek) {
        List<ScheduleTime> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isIstanbul = "istanbul".equals(this.city);
        String[] days = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        String dayCol = days[dayOfWeek - 1];

        String sql;
        String[] args;
        if (isIstanbul) {
            sql = "SELECT t.trip_headsign, st.departure_time FROM stop_times st JOIN trips t ON st.trip_id = t.trip_id " +
                    "JOIN calendar c ON t.service_id = c.service_id WHERE t.route_id = ? " +
                    "AND (st.stop_id = ? OR st.stop_id IN (SELECT stop_id FROM stops WHERE stop_code = ?)) " +
                    "AND c." + dayCol + " = 1 ORDER BY st.departure_time ASC";
            args = new String[]{routeId, stopId, stopId};
        } else {
            sql = "SELECT t.trip_headsign, st.departure_time FROM stop_times st JOIN trips t ON st.trip_id = t.trip_id " +
                    "JOIN calendar c ON t.service_id = c.service_id WHERE t.route_id = ? AND st.stop_id = ? " +
                    "AND c." + dayCol + " = 1 ORDER BY st.departure_time ASC";
            args = new String[]{routeId, stopId};
        }

        try (Cursor cursor = db.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                do { list.add(new ScheduleTime(cursor.getString(0), cursor.getString(1))); } while (cursor.moveToNext());
            }
        }
        return list;
    }

    private String getDayColumn() {
        String[] days = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        return days[java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) - 1];
    }
}
