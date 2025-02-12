package com.example.geocaching1.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.geocaching1.Geocache;
import com.example.geocaching1.R;

import java.util.List;

public class GeocacheAdapter extends RecyclerView.Adapter<GeocacheAdapter.ViewHolder> {

    private List<Geocache> geocacheList;

    public GeocacheAdapter(List<Geocache> geocacheList) {
        this.geocacheList = geocacheList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.geocache_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Geocache geocache = geocacheList.get(position);
        holder.textViewName.setText(geocache.getName());  // 显示 name
        holder.textViewType.setText("Type: " + geocache.getType());  // 显示 type
        holder.textViewStatus.setText("Status: " + geocache.getStatus());  // 显示 status
        holder.textViewLocation.setText("Location: " + geocache.getLatitude() + ", " + geocache.getLongitude());  // 显示位置
        holder.textViewFoundAt.setText("Found At: " + geocache.getFoundAt());  // 显示找到的时间
    }

    @Override
    public int getItemCount() {
        return geocacheList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewType;
        TextView textViewStatus;
        TextView textViewLocation;
        TextView textViewFoundAt;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewType = itemView.findViewById(R.id.textViewType);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewFoundAt = itemView.findViewById(R.id.textViewFoundAt);
        }
    }
}
