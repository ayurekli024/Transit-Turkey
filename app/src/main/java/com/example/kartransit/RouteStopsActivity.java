package com.example.kartransit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Eklendi
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RouteStopsActivity extends AppCompatActivity implements OnStopClickListener {
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private String selectedStopId;
    private String cityName;
    private Location currentUserLocation;
    private final android.os.Handler refreshHandler = new android.os.Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_stops);

        selectedStopId = getIntent().getStringExtra("SELECTED_STOP_ID");
        String routeId = getIntent().getStringExtra("ROUTE_ID");
        String routeName = getIntent().getStringExtra("ROUTE_NAME");
        cityName = getIntent().getStringExtra("CITY_NAME");

        if (cityName == null) cityName = "ankara";

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(routeName != null ? routeName + " Güzergahı" : "Güzergah");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerViewRouteStops);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DatabaseHelper(this, cityName);

        checkLocationPermission();

        if (routeId != null) {
            loadRouteData(routeId);
        }
    }

    private void startAutoRefresh(String routeFullName, String routeNo, String stopNo, String stopName) {
        if ("ankara".equals(cityName)) {
            refreshRunnable = new StringRunnable(routeFullName, routeNo, stopNo, stopName);
            refreshHandler.post(refreshRunnable);
        }
    }

    private class StringRunnable implements Runnable {
        String rFullName, rNo, sNo, sName;

        StringRunnable(String rFullName, String rNo, String sNo, String sName) {
            this.rFullName = rFullName;
            this.rNo = rNo;
            this.sNo = sNo;
            this.sName = sName;
        }

        @Override
        public void run() {
            fetchLiveBusData(rFullName, rNo, sNo, sName);
            refreshHandler.postDelayed(this, 30000);
            Log.d("EGO_REFRESH", "Veriler güncelleniyor...");
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            currentUserLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (currentUserLocation == null) {
                currentUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void loadRouteData(String routeId) {
        new Thread(() -> {
            final List<Stop> stops = dbHelper.getStopsByRouteId(routeId, selectedStopId);
            final String routeName = getIntent().getStringExtra("ROUTE_NAME");
            final String routeShortName = getIntent().getStringExtra("ROUTE_ID");

            runOnUiThread(() -> {
                if (stops != null && !stops.isEmpty()) {
                    StopAdapter adapter = new StopAdapter(stops, selectedStopId, currentUserLocation, this);
                    recyclerView.setAdapter(adapter);

                    if ("ankara".equals(cityName)) {
                        for (Stop stop : stops) {
                            if (stop.getStopId().equals(selectedStopId)) {
                                startAutoRefresh(routeName, routeShortName, stop.getStopId(), stop.getStopName());
                                break;
                            }
                        }
                    }
                }
            });
        }).start();
    }

    private void fetchLiveBusData(String routeFullName, String routeNo, String stopNo, String stopName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ego-web-service.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EgoApiService apiService = retrofit.create(EgoApiService.class);
        apiService.getLiveUpdate(stopNo, routeNo).enqueue(new Callback<EgoResponse>() {
            @Override
            public void onResponse(@NonNull Call<EgoResponse> call, @NonNull retrofit2.Response<EgoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EgoResponse egoData = response.body();
                    if ("aktif".equals(egoData.durum)) {
                        sendDataToWatch(routeFullName, stopName, egoData.sure);
                        runOnUiThread(() -> {
                            StopAdapter adapter = (StopAdapter) recyclerView.getAdapter();
                            if (adapter != null) {
                                adapter.updateLiveTime(stopNo, egoData.sure);
                                Log.d("EGO_LIVE", "Ekran güncellendi: " + egoData.sure);
                            }
                        });
                    } else {
                        Log.d("EGO_LIVE", "Aktif araç yok: " + egoData.mesaj);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EgoResponse> call, @NonNull Throwable t) {
                Log.e("EGO_LIVE", "API Hatası: " + t.getMessage());
            }
        });
    }

    private void sendDataToWatch(String routeName, String stopName, String minutes) {
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/bus_info");
        dataMapRequest.getDataMap().putString("route_name", routeName);
        dataMapRequest.getDataMap().putString("stop_name", stopName);
        dataMapRequest.getDataMap().putString("minutes", minutes);
        dataMapRequest.getDataMap().putLong("timestamp", System.currentTimeMillis());

        PutDataRequest request = dataMapRequest.asPutDataRequest();
        request.setUrgent();

        Wearable.getDataClient(this).putDataItem(request)
                .addOnSuccessListener(dataItem -> Log.d("Wear", "Saat verisi güncellendi: " + minutes + " dk"))
                .addOnFailureListener(e -> Log.e("Wear", "Saat hatası: " + e.getMessage()));
    }

    @Override
    public void onStopClick(Stop stop) {
        try {
            String uri = "google.navigation:q=" + stop.getStopLat() + "," + stop.getStopLon() + "(" + stop.getStopName() + ")";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Harita açılamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStopLongClick(Stop stop) {
        boolean isFav = dbHelper.isFavorite(stop.getStopId());
        String favText = isFav ? "Favorilerden Çıkar" : "Favorilere Ekle";
        String[] options = {favText, "İptal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Durak adının yanına ID bilgisini ekledik
        builder.setTitle(stop.getStopName() + " (" + stop.getStopId() + ")");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (isFav) {
                    dbHelper.removeFavorite(stop.getStopId());
                    Toast.makeText(this, "Favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addFavorite(stop);
                    Toast.makeText(this, "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
            String routeId = getIntent().getStringExtra("ROUTE_ID");
            if (routeId != null) loadRouteData(routeId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
            Log.d("EGO_REFRESH", "Otomatik yenileme durduruldu.");
        }
    }
}
