package com.example.kartransit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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

public class AnkaraFragment extends Fragment {

    private EditText stopSearchEditText;
    private Button searchButton;
    private Button cardButton;
    private Button mapButton;
    private Button nearbyButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ankara, container, false);

        stopSearchEditText = view.findViewById(R.id.editText_stopSearch);
        searchButton = view.findViewById(R.id.button_search);
        cardButton = view.findViewById(R.id.buttonCardOperations);
        mapButton = view.findViewById(R.id.buttonMetroMap);
        nearbyButton = view.findViewById(R.id.buttonNearbyStops);

        searchButton.setOnClickListener(v -> performStopSearch());
        nearbyButton.setOnClickListener(v -> checkLocationAndFindNearbyStops());
        
        cardButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardActivity.class);
            startActivity(intent);
        });
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MetroMapActivity.class);
            intent.putExtra("CITY_NAME", "Ankara");
            startActivity(intent);
        });

        addPlacesList(view);

        return view;
    }

    private void checkLocationAndFindNearbyStops() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        Location lastKnown = null;
        
        try {
            lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnown == null) {
                lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (lastKnown != null) {
            Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
            intent.putExtra("IS_NEARBY", true);
            intent.putExtra("USER_LAT", lastKnown.getLatitude());
            intent.putExtra("USER_LON", lastKnown.getLongitude());
            intent.putExtra("CITY_NAME", "ankara");
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Konum alınamadı. Lütfen GPS'i açın.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationAndFindNearbyStops();
        }
    }

    private void addPlacesList(View view) {
        LinearLayout placesContainer = view.findViewById(R.id.places_container);

        Map<String, List<String>> placesData = new LinkedHashMap<>();
        placesData.put("HASTANELER", Arrays.asList(
                "ETLİK ŞEHİR HASTANESİ", "BİLKENT ŞEHİR HASTANESİ", "GÜLHANE EĞİTİM VE ARAŞTIRMA HASTANESİ",
                "PURSAKLAR DEVLET HASTANESİ", "GÖLBAŞI DEVLET HASTANESİ", "BEYPAZARI DEVLET HASTANESİ",
                "SANATORYUM EĞİTİM ARAŞTIRMA HASTANESİ", "KIZILCAHAMAM DEVLET HASTANESİ",
                "SİNCAN EĞİTİM VE ARAŞTIRMA HASTANESİ", "MAMAK DEVLET HASTANESİ"
        ));
        placesData.put("PARKLAR", Arrays.asList(
                "ALTINPARK", "ANKAPARK", "MOGAN ATATÜRK SAHİL PARKI", "GÜVENPARK", "GENÇLİK PARKI",
                "KURTULUŞ PARKI", "HARİKALAR DİYARI", "KUĞULU PARK", "BAŞKENT MİLLET BAHÇESİ",
                "DİKMEN VADİSİ", "IHLAMUR VADİSİ", "SEĞMENLER PARKI", "MAVİ GÖL", "ÇUBUK-1 BARAJI",
                "KARAGÖL", "YAKACIK MESİRE ALANI"
        ));
        placesData.put("AVMLER", Arrays.asList(
                "ANKAMALL", "ATG-YHT", "FORUM ANKARA", "ANTARES AVM", "TAURUS AVM", "VEGA-SUBAYEVLERİ",
                "NATA VEGA", "ARMADA", "CEPA AVM", "KENTPARK", "FTZ", "KIZILAY AVM", "ACİTY", "OPTIMUM",
                "PANORA AVM"
        ));
        placesData.put("ÜNİVERSİTELER", Arrays.asList(
                "ODTÜ(ORTA DOĞU TEKNİK ÜNİVERSİTESİ)", "BİLKENT ÜNİVERSİTESİ", "HACETTEPE SIHHİYE YERLEŞKESİ","HACETTEPE BEYTEPE YERLEŞKESİ", "ANKARA ÜNİVERSİTESİ DİL, TARİH VE COĞRAFYA FAKÜLTESİ","ANKARA ÜNVERSİTESİ BEŞEVLER YERLEŞKESİ", "ANKARA ÜNİVERSİTESİ KEÇİÖREN YERLEŞKESİ",
                "YILDIRIM BEYAZIT ÜNİVERSİTESİ ETLİK YERLEŞKESİ","AYBÜ ÇUBUK YERLEŞLESİ","ANKARA ÜNİVERSİTESİ GÖLBAŞI YERLEŞKESİ","ANKARA ÜNİVERSİTESİ CEBECİ YERLEŞLESİ", "ANKARA ÜNİVERSİTESİ TIP FAKÜLTESİ",
                "ANKARA MEDİPOL ÜNİVERSİTESİ","BAŞKENT ÜNİVERSİTESİ","ÇANKAYA ÜNİVERSİTESİ","GAZİ ÜNİVERSİTESİ","HACI BAYRAM VELİ ÜNİVERSİTESİ","UFUK ÜNİVERSİTESİ","ANKARA BİLİM ÜNİVERSİTESİ","TOBB ÜNİVERSİTESİ"
        ));
        placesData.put("KÜLTÜREL YERLER", Arrays.asList(
                "MİLLİ KÜTÜPHANE", "CSO ADA ANKARA", "DEVLET TİYATROLARI ŞİNASİ SAHNESİ", "MEB ŞURA SALONU", "MİLLET KÜTÜPHANESİ", "CERMODERN",
                "ANITKABİR", "1. TBMM BİNASI", "ANKARA KALESİ", "ETNOGRAFYA MÜZESİ", "ANADOLU MEDENİYETLERİ MÜZESİ", "HACI BAYRAM VELİ CAMİİ", "ATAKULE",
                "ESTERGON KALESİ","ATO CONGRESSIUM"
        ));
        placesData.put("DİĞER", Arrays.asList(
                "CUMHURBAŞKANLIĞI KÜLLİYESİ", "BAKANLIKLAR", "TBMM", "HAVALİMANI", "OTOGAR", "ERYAMAN YHT",
                "ANKARA YHT GARI","KIZILAY", "SIHHİYE", "ULUS", "BAHÇELİEVLER"
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
            case "ETLİK ŞEHİR HASTANESİ":
                return "Ankara'nın en büyük ve en yeni entegre sağlık kompleksi. Birçok uzmanlık dalında hizmet vermektedir. Yüksek kapasitesi ve modern altyapısı ile dikkat çekmektedir.";
            case "BİLKENT ŞEHİR HASTANESİ":
                return "Avrupa'nın en büyük hastanelerinden biri olarak bilinen dev sağlık tesisi. 3700'den fazla yatak kapasitesine sahiptir ve birçok tıp fakültesi ile işbirliği yapmaktadır.\n" +
                        "M2 Koru Metrosunun Tarım Bakanlığı-Danıştay İstasyonunda inip ring otobüslerine binebilir veya,\n" +
                        "Kızılay veya Sıhhiye'den 112 numaralı otobüse binebilirsiniz.";
            case "GÜLHANE EĞİTİM VE ARAŞTIRMA HASTANESİ":
                return "Köklü geçmişe sahip, özellikle askeri tıp ve travma alanında önemli bir eğitim ve araştırma hastanesi." +
                        "263,267,284,285,286,288,414 nolu otobüsler ile hastaneye erişebilirsiniz.";
            case "ALTINPARK":
                return "Ankara'nın merkezinde yer alan büyük fuar, eğlence ve rekreasyon alanı. İçerisinde teleferik, gölet ve bilim merkezi gibi tesisler bulunur.";
            case "MOGAN ATATÜRK SAHİL PARKI":
                return "Gölbaşı ilçesinde bulunan Mogan Gölü kenarındaki büyük sahil parkı, yürüyüş yolları ve rekreasyon alanları ile ünlüdür." +
                        "104,105,114,115 hatları ile gündüz; 106 hattı ile gece parka erişebilirsiniz.";
            case "ANKAMALL":
                return "Gazi Mahallesi'nde bulunan, Ankara'nın en büyük ve eski alışveriş merkezlerinden biri. Geniş mağaza yelpazesi ile popülerdir.\n" +
                        "M1 Batıkent Metrosuna binip AKKöprü İstasyonundan buraya gidebilirsiniz.\n" +
                        "Gölbaşı veya Konya Yolu üstünden gelecekseniz, 104 veya 114 nolu otobüse binebilirsiniz.";
            case "FORUM ANKARA":
                return "261,263,267,284,285,286,289 hatları şehir merkezinden alışveriş merkezine erişim sağlayabilirsiniz.";
            case "ARMADA":
                return "Söğütözü'nde bulunan popüler ve lüks alışveriş merkezi. Çevre iş merkezlerine ve otellere yakın konumuyla bilinir." +
                        "M2 Koru Metrosu Söğütözü istasyonundan alışveriş merkezine erişebilirsiniz.";
            case "ODTÜ(ORTA DOĞU TEKNİK ÜNİVERSİTESİ)":
                return "Orta Doğu Teknik Üniversitesi. Türkiye'nin önde gelen teknik üniversitelerindendir. Geniş ormanlık kampüsü ve göleti ile ünlüdür." +
                        "Buraya M2 Koru Metrosuna binerek ODTÜ  istasyonundan ODTÜ A1 kapısına erişebilirsiniz.\n" +
                        "411 ve 482 nolu ÖHO araçlarıyla A4 kapısına gelebilirsiniz.";
            case "HACETTEPE BEYTEPE YERLEŞKESİ":
                return "Hacettepe Üniversitesi. Tıp and sağlık bilimleri alanında güçlü bir eğitim kurumudur. Beytepe ve Sıhhiye gibi birden fazla kampüse sahiptir." +
                        "\nM2 Metrosuna binip Beytepe istasyonundan 130 ring hattına binerek erişebilirsiniz.";
            case "HAVALİMANI":
                return "Ankara Esenboğa Havalimanı'na erişebilmek için 442 nolu hata veya HAVAŞ otobüslerine binebilirsiniz.";
            case "ANKARA ÜNİVERSİTESİ GÖLBAŞI YERLEŞKESİ":
                return "AKKÖPRÜ VE AŞTİ'DEN: 104 Veya 114 otobüslerine binebilirsiniz, 114 hattı daha hızlıdır." +
                        "\nUlus, Sıhhiye veya Kızılay'dan: 105,106,115,195 otobüslerine binebilirsiniz, 115 en hızlısıdır.";
            case "CSO ADA ANKARA":
                return "M1 Sıhhiye İstasyonu, BAŞKENTRAY Yenişehir İstasyonu veya M4 Adliye istasyonundan erişebilirsiniz.\n" +
                        "Kızılay Meydanından yürüyerek 25 dk da ulaşabilirsiniz.";
            case "MİLLİ KÜTÜPHANE":
                return "M2 Metrosunun Milli Kütüphane İstasyonu'nda inip ulaşabilirsiniz.";
            case "ATO CONGRESSIUM":
                return "M2 Metrosunun Söğütözü İstasyonu'nda inip ulaşabilirsiniz.";
            case "BAHÇELİEVLER":
                return "M2 Metrosunun Milli Kütüphane İstasyonu'nda inip ulaşabilirsiniz." +
                        "Ankaray hattının Beşevler istasyonundan erişebilirsiniz.";
            case "KENTPARK":
                return "M2 Metrosunun ODTÜ veya Bilkent İstasyonu'nda inip 10 dakika yürüme mesafesinde ulaşabilirsiniz." +
                        "112 ve 339-5 hatları tam önünde durmaktadır.";
            case "CEPA AVM":
                return "M2 Metrosunun ODTÜ veya Bilkent İstasyonu'nda inip 10 dakika yürüme mesafesinde ulaşabilirsiniz." +
                        "112 ve 339-5 hatları tam önünde durmaktadır.";
            case "ANITKABİR":
                return "Ankaray hatının ANADOLU/ANITKABİR İstasyonu'nda inip ulaşabilirsiniz.";
            case "ANKARA YHT GARI":
                return "M4 Keçiören Metrosunun GAR istasyonundan erişebilirsiniz.\n" +
                        "Sıhhiye Köprüsünün üstündeki çoğu otobüs önünden geçer.\n" +
                        "Başkentray hattının ANKARA GAR istasyonundan gelebilirsiniz.";
            case "OTOGAR":
                return "AŞTİ otogarına gitmek için ANKARAY hattında AŞTİ istasyonunda inebilirsiniz.\n" +
                        "Eğer ANKARAY hattına binmeniz zor ise, 104,114 gibi AKKöprü Otobüslerine binip otogara erişebilirsiniz.";
            default:
                return itemName + " hakkında detaylı bilgiye ulaşmak için lütfen internet bağlantınızı kontrol edin. Bu yer, Ankara'nın önemli noktalarından biridir.";
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
        intent.putExtra("CITY_NAME", "ankara");
        startActivity(intent);
    }
}
