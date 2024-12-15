
package com.example.geocaching1;


import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPrivacyDialog();
        // 初始化地图视图
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须调用

        // 获取 AMap 对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 设置地图视角
        LatLng latLng = new LatLng(39.908, 116.397);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        aMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Beijing"));
    }

    private void showPrivacyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Privacy Policy");
        builder.setMessage("We collect and use your location information to provide more accurate services. Please read and agree to the privacy policy to continue using the app.");
        builder.setPositiveButton("Agree", (dialog, which) -> {
            // User agrees
            AMapLocationClient.updatePrivacyShow(this, true, true);
            AMapLocationClient.updatePrivacyAgree(this, true);
            dialog.dismiss();
        });
        builder.setNegativeButton("Disagree", (dialog, which) -> {
            // User disagrees
            Toast.makeText(this, "You need to agree to the privacy policy to use the location feature", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setCancelable(false);
        builder.show();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}