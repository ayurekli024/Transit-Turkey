package com.example.kartransit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity implements OnStopClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DatabaseHelper dbHelper;
    private String searchQuery;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        cityName = getIntent().getStringExtra("CITY_NAME");
        if (cityName == null) cityName = "ankara";

        if (getSupportActionBar() != null) {
            String cityTitle = cityName.substring(0, 1).toUpperCase() + cityName.substring(1);
            getSupportActionBar().setTitle("Arama Sonuçları (" + cityTitle + ")");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerViewStopResults);
        progressBar = findViewById(R.id.progressBarLoading);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DatabaseHelper(this, cityName);

        searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
        if (searchQuery != null) {
            startSearch(searchQuery);
        }
    }

    private void startSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            final List<Stop> results = dbHelper.searchStops(query);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (results != null && !results.isEmpty()) {
                    StopAdapter adapter = new StopAdapter(results, null, null, this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "Sonuç bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onStopClick(Stop stop) {
        Intent intent = new Intent(this, RouteDetailsActivity.class);
        intent.putExtra("STOP_ID", stop.getStopId());
        intent.putExtra("STOP_NAME", stop.getStopName());
        intent.putExtra("CITY_NAME", cityName);
        startActivity(intent);
    }

    @Override
    public void onStopLongClick(Stop stop) {
        boolean isFav = dbHelper.isFavorite(stop.getStopId());
        String favText = isFav ? "Favorilerden Çıkar" : "Favorilere Ekle";
        String[] options = {favText, "İptal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(stop.getStopName());
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
}
