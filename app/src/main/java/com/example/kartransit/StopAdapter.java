package com.example.kartransit;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {

    private final List<Stop> stopList;
    private final OnStopClickListener listener;
    private final String selectedStopId;
    private final Location userLocation;
    private int selectedPosition = -1;

    public List<Stop> getStopList() {
        return stopList;
    }

    public StopAdapter(List<Stop> stopList, String selectedStopId, Location userLocation, OnStopClickListener listener) {
        this.stopList = stopList;
        this.selectedStopId = selectedStopId;
        this.userLocation = userLocation;
        this.listener = listener;
        updateSelectedPosition();
    }

    public void updateLiveTime(String stopId, String liveTime) {
        for (int i = 0; i < stopList.size(); i++) {
            if (stopList.get(i).getStopId().equals(stopId)) {
                stopList.get(i).setLiveDepartureTime(liveTime);
                notifyItemChanged(i);
                break;
            }
        }
    }

    private void updateSelectedPosition() {
        if (selectedStopId == null) return;
        for (int i = 0; i < stopList.size(); i++) {
            if (stopList.get(i).getStopId().equals(selectedStopId)) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stop, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        Stop currentStop = stopList.get(position);

        holder.stopNameTextView.setText(currentStop.getStopName());

        int statusColor;
        boolean isPassed = false;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String nowStr = sdf.format(new Date());
            Date currentTime = sdf.parse(nowStr);
            Date stopTime = sdf.parse(currentStop.getDepartureTime());

            if (currentTime != null && stopTime != null) {
                isPassed = stopTime.before(currentTime);
            }
        } catch (Exception e) {
            isPassed = (selectedPosition != -1 && position < selectedPosition);
        }

        if (selectedPosition != -1 && position == selectedPosition) {
            statusColor = Color.parseColor("#1E88E5");
        } else if (isPassed) {
            statusColor = Color.parseColor("#E53935");
        } else {
            statusColor = Color.parseColor("#43A047");
        }

        boolean isBusHere = false;
        if (isPassed && position + 1 < stopList.size()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date currentTime = sdf.parse(sdf.format(new Date()));
                Date nextStopTime = sdf.parse(stopList.get(position + 1).getDepartureTime());
                if (nextStopTime != null && nextStopTime.after(currentTime)) {
                    isBusHere = true;
                }
            } catch (Exception e) {
                isBusHere = (position + 1 == selectedPosition);
            }
        }

        if (isBusHere) {
            holder.imageViewBus.setVisibility(View.VISIBLE);
            holder.imageViewBus.setColorFilter(Color.BLACK);
            holder.imageViewBus.setElevation(10f);

            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params =
                    (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) holder.imageViewBus.getLayoutParams();
            params.verticalBias = 0.5f;
            holder.imageViewBus.setLayoutParams(params);
            startBusAnimation(holder.imageViewBus);
        } else {
            holder.imageViewBus.setVisibility(View.GONE);
            holder.imageViewBus.clearAnimation();
        }

        holder.stopNameTextView.setTextColor(statusColor);

        if (holder.viewDot != null && holder.viewLine != null) {
            holder.viewDot.setBackgroundResource(R.drawable.circle_shape);
            holder.viewDot.getBackground().setColorFilter(statusColor, android.graphics.PorterDuff.Mode.SRC_IN);
            holder.viewLine.setBackgroundColor(statusColor);
            holder.viewLine.setElevation(1f);
            holder.viewDot.setElevation(2f);
        }

        if (selectedStopId != null && currentStop.getStopId().equals(selectedStopId)) {
            holder.stopNameTextView.setTypeface(null, Typeface.BOLD);
        } else {
            holder.stopNameTextView.setTypeface(null, Typeface.NORMAL);
        }

        calculateInfo(holder, currentStop);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStopClick(currentStop);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if(listener != null) {
                listener.onStopLongClick(currentStop);
            }
            return true;
        });
    }

    private void startBusAnimation(View view) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -15);
        anim.setDuration(500);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }

    private void calculateInfo(StopViewHolder holder, Stop currentStop) {
        if (currentStop.getLiveDepartureTime() != null) {
            holder.stopDetailsTextView.setText("🔴 CANLI: " + currentStop.getLiveDepartureTime());
            holder.stopDetailsTextView.setTextColor(Color.parseColor("#FFD700"));
            holder.stopDetailsTextView.setTypeface(null, Typeface.BOLD);
            return;
        }

        StringBuilder infoText = new StringBuilder();
        holder.stopDetailsTextView.setTextColor(Color.GRAY);
        holder.stopDetailsTextView.setTypeface(null, Typeface.NORMAL);

        if (currentStop.getDepartureTime() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date currentTime = sdf.parse(sdf.format(new Date()));
                Date stopTime = sdf.parse(currentStop.getDepartureTime());

                if (currentTime != null && stopTime != null) {
                    long diffInMin = (stopTime.getTime() - currentTime.getTime()) / (1000 * 60);
                    if (diffInMin > 0) infoText.append(diffInMin).append(" dk sonra");
                    else if (diffInMin == 0) infoText.append("Durağa yaklaşıyor");
                    else infoText.append("Otobüs geçti");
                }
            } catch (Exception e) {
                infoText.append(currentStop.getDepartureTime());
            }
        }

        if (selectedStopId != null && currentStop.getStopId().equals(selectedStopId) && userLocation != null) {
            Location stopLoc = new Location("");
            stopLoc.setLatitude(currentStop.getStopLat());
            stopLoc.setLongitude(currentStop.getStopLon());
            float distance = userLocation.distanceTo(stopLoc);
            String distStr = (distance < 1000) ?
                    String.format(Locale.getDefault(), " (%.0f m uzakta)", distance) :
                    String.format(Locale.getDefault(), " (%.1f km uzakta)", distance / 1000f);
            infoText.append(distStr);
        }
        holder.stopDetailsTextView.setText(infoText.toString());
    }

    @Override
    public int getItemCount() {
        return stopList != null ? stopList.size() : 0;
    }

    static class StopViewHolder extends RecyclerView.ViewHolder {
        TextView stopNameTextView, stopDetailsTextView;
        View viewLine, viewDot;
        ImageView imageViewBus;

        StopViewHolder(View v) {
            super(v);
            stopNameTextView = v.findViewById(R.id.textViewStopName);
            stopDetailsTextView = v.findViewById(R.id.textViewStopDetails);
            viewLine = v.findViewById(R.id.view_line);
            viewDot = v.findViewById(R.id.view_dot);
            imageViewBus = v.findViewById(R.id.imageViewBus);
        }
    }
}
