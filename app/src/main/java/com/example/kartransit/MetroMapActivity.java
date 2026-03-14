package com.example.kartransit;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.widget.ImageView;
import io.getstream.photoview.PhotoView;

public class MetroMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metro_map);

        // XML'den PhotoView bileşenini al
        PhotoView photoView = findViewById(R.id.metroPhotoView);

        // Intent'ten hangi şehrin haritasının istendiğini öğren
        String cityName = getIntent().getStringExtra("CITY_NAME");

        int mapResource;
        String title;

        if ("Istanbul".equals(cityName)) {
            mapResource = R.drawable.istanbul_haritasi;
            title = "İstanbul Metro Haritası";
        } else if ("Izmir".equals(cityName)) {
            mapResource = R.drawable.izmir_haritasi;
            title = "İzmir Metro Haritası";
        }
        else {
            mapResource = R.drawable.metro_haritasi;
            title = "Ankara Metro Haritası";
        }

        // Başlık ve Resmi Ayarlama
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        try {
            photoView.setImageResource(mapResource);
        } catch (Exception e) {
            Toast.makeText(this, "Hata: Harita dosyası bulunamadı. Lütfen drawable klasörünü kontrol edin.", Toast.LENGTH_LONG).show();
            Log.e("MAP_LOAD", "Harita yüklenemedi: " + title, e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}