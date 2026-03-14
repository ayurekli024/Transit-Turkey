package com.example.kartransit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.util.List;

import com.example.kartransit.RouteAdapter.OnRouteClickListener;

public class RouteDetailsActivity extends AppCompatActivity implements OnRouteClickListener {

    private TextView stopNameTextView;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    private String selectedStopId;
    private String currentStopName;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        stopNameTextView = findViewById(R.id.textViewDetailStopName);
        recyclerView = findViewById(R.id.recyclerViewRouteList);

        Intent intent = getIntent();
        if (intent != null) {
            selectedStopId = intent.getStringExtra("STOP_ID");
            currentStopName = intent.getStringExtra("STOP_NAME");
            cityName = intent.getStringExtra("CITY_NAME");
        }

        if (cityName == null) cityName = "ankara";

        if (currentStopName != null) {
            stopNameTextView.setText(currentStopName);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Hatlar: " + currentStopName);
            }
        } else {
            stopNameTextView.setText("Durak Bilgisi Yok");
        }

        dbHelper = new DatabaseHelper(this, cityName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (selectedStopId != null) {
            displayRoutesByStopId(selectedStopId);
        } else {
            Toast.makeText(this, "Durak ID'si alınamadı.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRouteClick(Route route) {
        Intent intent = new Intent(this, RouteStopsActivity.class);
        intent.putExtra("ROUTE_ID", route.getRouteId());
        intent.putExtra("ROUTE_NAME", route.getRouteShortName());
        intent.putExtra("SELECTED_STOP_ID", this.selectedStopId);
        intent.putExtra("CITY_NAME", cityName);

        Log.d("NAV_CHECK", "Güzergaha gönderilen Durak ID: " + selectedStopId);
        startActivity(intent);
    }

    private void displayRoutesByStopId(String stopId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Route> routes = dbHelper.getRoutesByStopId(stopId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (routes != null && !routes.isEmpty()) {
                            RouteAdapter adapter = new RouteAdapter(routes, RouteDetailsActivity.this);
                            recyclerView.setAdapter(adapter);

                            Toast.makeText(RouteDetailsActivity.this,
                                    "Bulunan " + routes.size() + " adet hat listeleniyor.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RouteDetailsActivity.this,
                                    "Veritabanında bu durak için hat bulunamadı.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
