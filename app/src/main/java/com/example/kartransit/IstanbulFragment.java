package com.example.kartransit;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.util.TypedValue;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IstanbulFragment extends Fragment {

    private EditText stopSearchEditText;
    private Button searchButton;
    private Button cardButton;
    private Button mapButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_istanbul, container, false);

        stopSearchEditText = view.findViewById(R.id.editText_stopSearch);
        searchButton = view.findViewById(R.id.button_search);
        cardButton = view.findViewById(R.id.buttonCardOperations);
        mapButton = view.findViewById(R.id.buttonMetroMap);

        searchButton.setOnClickListener(v -> performStopSearch());
        cardButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), IstanbulCardActivity.class);
            startActivity(intent);
        });

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MetroMapActivity.class);
            intent.putExtra("CITY_NAME", "Istanbul");
            startActivity(intent);
        });

        addPlacesList(view);

        return view;
    }

    private void addPlacesList(View view) {
        LinearLayout placesContainer = view.findViewById(R.id.places_container);

        Map<String, List<String>> placesData = new LinkedHashMap<>();
        placesData.put("HASTANELER", Arrays.asList(
                "BAŞAKŞEHİR ÇAM VE SAKURA ŞEHİR HASTANESİ", "SANCAKTEPE ŞEHİR HASTANESİ", "PENDİK EĞİTİM ARAŞTIRMA HASTANESİ",
                "GAZİOSMANPAŞA DEVLET HASTANESİ","BAHÇELİEVLER DEVLET HASTANESİ","ÜMRANİYE EĞİTİM ARAŞTIRMA HASTANESİ"
        ));
        placesData.put("PARKLAR", Arrays.asList(
                "GÜLHANE PARKI"
        ));
        placesData.put("AVMLER", Arrays.asList(
                "VADİ İSTANBUL", "MALL OF İSTANBUL","VIALAND İSTANBUL","FORUM İSTANBUL",
                "ZORLU CENTER","EMAAR SQUARE AVM","METROPOL AVM"
        ));
        placesData.put("TURİSTİK YERLER", Arrays.asList(
                "AYASOFYA CAMİİ", "SULTANAHMET CAMİİ","İSTİKLAL CADDESİ","TAKSİM","KAPALI ÇARŞI","SULTANAHMET MEYDANI","TOPKAPI SARAYI",
                "YILDIZ SARAYI","HAYDARPAŞA GARI"
        ));
        placesData.put("ÜNİVERSİTELER", Arrays.asList(
                "İSTANBUL TEKNİK ÜNİVERSİTESİ AYAZAĞA KAMPÜSÜ", "İSTANBUL ÜNİVERSİTESİ", "İSTANBUL MEDİPOL ÜNİVERSİTESİ","YILDIZ TEKNİK ÜNİVERSİTESİ", "MARMARA ÜNİVERSİTESİ",
                "YEDİTEPE ÜNİVERSİTESİ","KOÇ ÜNİVERSİTESİ","SABANCI ÜNİVERSİTESİ","BOĞAZİÇİ ÜNİVERSİTESİ","SAĞLIK BİLİMLERİ ÜNİVERSİTESİ"
        ));
        placesData.put("STADLAR", Arrays.asList(
                "RAMS PARK","ATATÜRK OLİMPİYAT STADI"
        ));
        placesData.put("DİĞER", Arrays.asList(
                "ATATÜRK HAVALİMANI","SABİHA GÖKÇEN HAVALİMANI"
        ));

        for (Map.Entry<String, List<String>> entry : placesData.entrySet()) {
            TextView categoryTitle = new TextView(getContext());
            categoryTitle.setText(entry.getKey().toUpperCase());
            categoryTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            categoryTitle.setTypeface(null, Typeface.BOLD);
            categoryTitle.setPadding(0, 30, 0, 10);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            placesContainer.addView(categoryTitle, params);

            for (String item : entry.getValue()) {
                TextView itemText = new TextView(getContext());
                itemText.setText("• " + item);
                itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                itemText.setPadding(20, 5, 0, 5);
                itemText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));

                itemText.setOnClickListener(v -> {
                    showDescriptionPopup(item);
                });

                placesContainer.addView(itemText);
            }
        }
    }

    private void showDescriptionPopup(String itemName) {
        if (getContext() == null) return;

        String description = getDescription(itemName);

        new AlertDialog.Builder(getContext())
                .setTitle(itemName)
                .setMessage(description)
                .setPositiveButton("Kapat", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private String getDescription(String itemName) {
        switch (itemName) {
            default:
                return itemName + " hakkında detaylı bilgiye ulaşmak için lütfen internet bağlantınızı kontrol edin. Bu yer, İstanbul'un önemli noktalarından biridir.";
        }
    }

    private void performStopSearch() {
        String query = stopSearchEditText.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getActivity(), "Lütfen bir durak numarası veya adı girin.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
        intent.putExtra("SEARCH_QUERY", query);
        intent.putExtra("CITY_NAME", "istanbul");
        startActivity(intent);
    }
}
