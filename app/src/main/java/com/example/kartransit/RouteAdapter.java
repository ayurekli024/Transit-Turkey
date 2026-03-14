package com.example.kartransit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    public interface OnRouteClickListener {
        void onRouteClick(Route route);
    }

    private final List<Route> routeList;
    private final OnRouteClickListener listener;

    public RouteAdapter(List<Route> routeList, OnRouteClickListener listener) {
        this.routeList = routeList;
        this.listener = listener;
    }



    static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView routeNumberTextView;
        TextView routeNameTextView;

        RouteViewHolder(View itemView) {
            super(itemView);
            routeNameTextView = itemView.findViewById(R.id.textViewRouteName);
            routeNumberTextView = itemView.findViewById(R.id.textViewRouteNumber);
        }
    }
    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route currentRoute = routeList.get(position);

        holder.routeNumberTextView.setText(currentRoute.getRouteShortName());
        holder.routeNameTextView.setText(currentRoute.getRouteLongName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRouteClick(currentRoute);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }
}