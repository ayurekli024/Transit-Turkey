package com.example.kartransit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements OnStopClickListener {

    private RecyclerView recyclerView;
    private List<Stop> allFavoritesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Favori Duraklarım");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAllFavorites();
    }

    private void loadAllFavorites() {
        allFavoritesList.clear();
        String[] cities = {"ankara", "istanbul", "izmir"};
        
        for (String city : cities) {
            DatabaseHelper db = new DatabaseHelper(this, city);
            List<Stop> cityFavs = db.getAllFavorites();
            if (cityFavs != null) {
                // Her durağın hangi şehre ait olduğunu bildiğimizden emin olalım
                for (Stop s : cityFavs) {
                    if (s.getCityName() == null) s.setCityName(city);
                }
                allFavoritesList.addAll(cityFavs);
            }
        }

        if (!allFavoritesList.isEmpty()) {
            StopAdapter adapter = new StopAdapter(allFavoritesList, null, null, this);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Henüz favori durak eklemediniz.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStopClick(Stop stop) {
        Intent intent = new Intent(this, RouteDetailsActivity.class);
        intent.putExtra("STOP_ID", stop.getStopId());
        intent.putExtra("STOP_NAME", stop.getStopName());
        // Durak nesnesinde kayıtlı olan şehri kullan
        String city = stop.getCityName();
        if (city == null) city = "ankara"; 
        intent.putExtra("CITY_NAME", city);
        startActivity(intent);
    }

    @Override
    public void onStopLongClick(Stop stop) {
        new AlertDialog.Builder(this)
                .setTitle(stop.getStopName())
                .setMessage("Bu durağı favorilerden kaldırmak istiyor musunuz?")
                .setPositiveButton("Kaldır", (dialog, which) -> {
                    String city = stop.getCityName();
                    if (city == null) city = "ankara";
                    DatabaseHelper db = new DatabaseHelper(this, city);
                    db.removeFavorite(stop.getStopId());
                    loadAllFavorites();
                    Toast.makeText(this, "Kaldırıldı", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
