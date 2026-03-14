package com.example.kartransit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.ParseException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private TextView routeNameTextView;
    private RecyclerView recyclerView;
    private Spinner daySpinner;
    private DatabaseHelper dbHelper;
    private String routeId;
    private String routeName;
    private String stopId;
    private String cityName;

    private static final String[] DAYS_OF_WEEK_NAMES =
            {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        routeNameTextView = findViewById(R.id.textViewScheduleRouteName);
        recyclerView = findViewById(R.id.recyclerViewScheduleTimes);
        daySpinner = findViewById(R.id.spinnerDay);

        routeId = getIntent().getStringExtra("ROUTE_ID");
        routeName = getIntent().getStringExtra("ROUTE_NAME");
        stopId = getIntent().getStringExtra("STOP_ID");
        cityName = getIntent().getStringExtra("CITY_NAME");

        if (cityName == null) cityName = "ankara";

        if (routeName != null) {
            routeNameTextView.setText(routeName + " Hattı Sefer Saatleri");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Hat: " + routeName);
            }
        }

        dbHelper = new DatabaseHelper(this, cityName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DAYS_OF_WEEK_NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int dayOfWeek = position + 2;
                loadScheduleAsync(routeId, stopId, dayOfWeek);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadScheduleAsync(routeId, stopId, 2);
            }
        });
    }


    private void loadScheduleAsync(String routeId, String stopId, int dayOfWeek) {
        if (routeId == null || routeId.isEmpty()) {
            Toast.makeText(this, "Geçerli Hat ID'si yok.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ScheduleTime> schedule = dbHelper.getSchedule(routeId, stopId, dayOfWeek);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (schedule != null && !schedule.isEmpty()) {

                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                            ScheduleTime nextDeparture = null;
                            long minutesRemaining = -1;
                            long currentTimeMillis = System.currentTimeMillis();

                            for (ScheduleTime scheduleTime : schedule) {
                                try {
                                    Date departureDate = timeFormat.parse(scheduleTime.getDepartureTime());

                                    Calendar calDeparture = Calendar.getInstance();
                                    Calendar calNow = Calendar.getInstance();

                                    calDeparture.setTime(departureDate);

                                    calDeparture.set(Calendar.YEAR, calNow.get(Calendar.YEAR));
                                    calDeparture.set(Calendar.MONTH, calNow.get(Calendar.MONTH));
                                    calDeparture.set(Calendar.DAY_OF_MONTH, calNow.get(Calendar.DAY_OF_MONTH));


                                    if (calDeparture.getTimeInMillis() > calNow.getTimeInMillis()) {
                                        nextDeparture = scheduleTime;
                                        long diffMillis = calDeparture.getTimeInMillis() - calNow.getTimeInMillis();
                                        minutesRemaining = diffMillis / (60 * 1000);
                                        break;
                                    }
                                } catch (ParseException e) {
                                    Log.e("TIME_CALC", "Saati ayrıştırma hatası: " + e.getMessage());
                                }
                            }

                            List<ScheduleTime> displayList = new ArrayList<>();

                            if (nextDeparture != null && minutesRemaining >= 0) {
                                String nextHeadsign = nextDeparture.getHeadsign();
                                String message = "SONRAKİ SEFER: " + (nextHeadsign != null ? nextHeadsign : "") +
                                        " (" + minutesRemaining + " DK)";
                                displayList.add(new ScheduleTime(message, nextDeparture.getDepartureTime()));
                                displayList.addAll(schedule);
                            } else {
                                displayList.add(new ScheduleTime("Bugün için daha fazla sefer kalmadı.", timeFormat.format(new Date())));
                                displayList.addAll(schedule);
                            }

                            ScheduleAdapter adapter = new ScheduleAdapter(displayList);
                            recyclerView.setAdapter(adapter);

                        } else {
                            recyclerView.setAdapter(null);
                            Toast.makeText(ScheduleActivity.this, "Seçilen gün için sefer bulunamadı.", Toast.LENGTH_LONG).show();
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
