package com.example.kartransit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        // Başlangıç fragment'ı olarak Ankara'yı ayarla
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AnkaraFragment())
                    .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
    }
    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_ankara) {
                    selectedFragment = new AnkaraFragment();
                } else if (itemId == R.id.nav_istanbul) {
                    selectedFragment = new IstanbulFragment();
                } else if (itemId == R.id.nav_izmir) {
                    selectedFragment = new IzmirFragment();
                } else if (itemId == R.id.nav_favorites) {
                    // Favoriler butonuna tıklandığında FavoritesActivity'yi başlat
                    startActivity(new Intent(this, FavoritesActivity.class));
                    return false; // Fragment değişimini engelle
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true; // Başarılı fragment değişimi
                }
                return false; // Eğer bir işlem yapılmadıysa
            };
}
