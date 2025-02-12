package com.example.geocaching1;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.geocaching1.R;
import com.example.geocaching1.ui.adapter.GeocacheAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GeocacheAdapter adapter;
    private List<Geocache> geocacheList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        adapter = new GeocacheAdapter(geocacheList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchGeocaches();
    }

    private void fetchGeocaches() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                String response = GeocacheFetcher.fetchGeocaches(0.0, 0.0); // 使用实际经纬度
                if (response != null && !response.isEmpty()) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        updateGeocacheList(response);
                    });
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(DashboardActivity.this, "No geocaches found.", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DashboardActivity.this, "Error fetching geocaches: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                e.printStackTrace();
            }
        }).start();
    }


    private void updateGeocacheList(String response) {
        try {
            Log.d("DashboardActivity", "Response: " + response);  // 打印API返回的响应
            JSONArray geocacheArray = new JSONArray(response);
            geocacheList.clear();  // 清除旧数据
            for (int i = 0; i < geocacheArray.length(); i++) {
                JSONObject geocacheObject = geocacheArray.getJSONObject(i);
                String code = geocacheObject.getString("code");
                String name = geocacheObject.getString("name");
                String location = geocacheObject.getString("location");
                String status = geocacheObject.getString("status");
                String type = geocacheObject.getString("type");
                String foundAtString = geocacheObject.optString("foundAt", LocalDateTime.now().toString());
                LocalDateTime foundAt = LocalDateTime.parse(foundAtString);

                // 假设location是"latitude|longitude"格式
                String[] locationParts = location.split("\\|");
                BigDecimal latitude = new BigDecimal(locationParts[0]);
                BigDecimal longitude = new BigDecimal(locationParts[1]);

                Geocache geocache = new Geocache(code, name, latitude, longitude, status, type, foundAt);
                geocacheList.add(geocache);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(DashboardActivity.this, "Error parsing geocaches data.", Toast.LENGTH_SHORT).show();
        }
    }

}
