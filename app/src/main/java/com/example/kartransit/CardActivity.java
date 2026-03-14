package com.example.kartransit;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Başkentkart veya Ankarakart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button checkBalanceButton = findViewById(R.id.buttonCheckBalance);
        Button topUpButton = findViewById(R.id.buttonTopUp);

        checkBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resmî bakiye sorgulama sayfasına yönlendirme
                String url = "https://baskentulasim.com/";
                openUrl(url);
            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://baskentulasim.com/";
                openUrl(url);
            }
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