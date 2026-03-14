package com.example.kartransit;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;

// Wearable API Importları
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
public class MainActivity extends Activity implements DataClient.OnDataChangedListener {
    private TextView textRoute, textStop, textMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textRoute = findViewById(R.id.text_route);
        textStop = findViewById(R.id.text_stop);
        textMinutes = findViewById(R.id.text_minutes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if ("/bus_info".equals(item.getUri().getPath())) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    // Verileri güvenli bir şekilde alalım
                    String route = dataMap.getString("route_name", "Bilinmeyen Hat");
                    String stop = dataMap.getString("stop_name", "Bilinmeyen Durak");
                    String minutes = dataMap.getString("minutes", "--");

                    android.util.Log.d("WearWatch", "Gelen -> Hat: " + route + " Durak: " + stop + " Dakika: " + minutes);

                    runOnUiThread(() -> {
                        if (textRoute != null) textRoute.setText(route);
                        if (textStop != null) textStop.setText(stop); // Bu satır eksikti, eklendi.
                        if (textMinutes != null) textMinutes.setText(minutes);
                    });
                }
            }
        }
    }
}
