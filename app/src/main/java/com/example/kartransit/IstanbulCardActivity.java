package com.example.kartransit;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class IstanbulCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istanbul_card);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("İstanbulkart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button checkBalanceButton = findViewById(R.id.buttonCheckBalanceIstanbul);
        Button topUpButton = findViewById(R.id.buttonTopUpIstanbul);

        // Bakiye Sorgulama Olayı (Örnek URL: İstanbulkart web sitesi)
        checkBalanceButton.setOnClickListener(v -> {
            String url = "https://bireysel.istanbulkart.istanbul/";
            openUrl(url);
        });

        // Para Yükleme Olayı (Örnek URL: İstanbulkart web sitesi)
        topUpButton.setOnClickListener(v -> {
            String url = "https://bireysel.istanbulkart.istanbul/";
            openUrl(url);
        });
    }

    private void openUrl(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Tarayıcı açılamadı: " + url, Toast.LENGTH_LONG).show();
        }
    }

    // Geri tuşu işlevi
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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