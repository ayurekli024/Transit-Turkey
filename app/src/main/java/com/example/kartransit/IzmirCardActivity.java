package com.example.kartransit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class IzmirCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izmir_card);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("İzmirimkart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button checkBalanceButtonIzmir = findViewById(R.id.buttonCheckBalanceizmir);
        Button topUpButton = findViewById(R.id.buttonTopUpizmir);
        Button checkIzbanButton = findViewById(R.id.buttonCheckizban);

        checkBalanceButtonIzmir.setOnClickListener(v -> {
            String url = "https://izmirimkart.com.tr/giris";
            openUrl(url);
        });

        topUpButton.setOnClickListener(v -> {
            String url = "https://www.eshot.gov.tr/tr/GittiginKadarOde/288";
            openUrl(url);
        });
        checkIzbanButton.setOnClickListener(v -> {
            String url = "https://tarife.izmirimkart.com.tr/";
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