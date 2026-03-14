package com.example.kartransit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<ScheduleTime> scheduleList;

    public ScheduleAdapter(List<ScheduleTime> scheduleList) {
        this.scheduleList = scheduleList;
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView departureTimeTextView;
        TextView headsignTextView;

        ScheduleViewHolder(View itemView) {
            super(itemView);
            departureTimeTextView = itemView.findViewById(R.id.textViewDepartureTime);
            headsignTextView = itemView.findViewById(R.id.textViewHeadsign);
        }
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleTime currentTime = scheduleList.get(position);

        String timeDisplay = currentTime.getDepartureTime().substring(0, 5);

        holder.departureTimeTextView.setText(timeDisplay);

        holder.headsignTextView.setText(currentTime.getHeadsign());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }
}