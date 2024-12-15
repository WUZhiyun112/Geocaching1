//package com.example.geocaching1;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.amap.api.maps2d.AMap;
//import com.amap.api.maps2d.MapView;
//import com.amap.api.maps2d.CameraUpdateFactory;
//import com.amap.api.maps2d.model.LatLng;
//import com.amap.api.maps2d.model.MarkerOptions;
//
//public class MainActivity extends AppCompatActivity {
//
//    private MapView mapView;
//    private AMap aMap;
//    private AMapLocationClient locationClient; // Location client
//    private AMapLocationClientOption locationOption; // Location settings
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize the map view
//        mapView = findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState); // This must be called
//
//
//
//            // 更新隐私合规
//          showPrivacyDialog();
//
//            // 初始化 AMapLocationClient
////            AMapLocationClient locationClient = new AMapLocationClient(getApplicationContext());
//
//
//        // Get AMap object
//        if (aMap == null) {
//            aMap = mapView.getMap();
//        }
//
//        // Initialize location client
////        try {
////            locationClient = new AMapLocationClient(getApplicationContext());
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////        locationOption = new AMapLocationClientOption();
//
//        // Set location mode
//        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        locationOption.setNeedAddress(true); // Whether to return address info
//        locationOption.setOnceLocation(true); // Get location once
//
//        // Set location listener
//        locationClient.setLocationListener(new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation aMapLocation) {
//                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
//                    // Location success
//                    double latitude = aMapLocation.getLatitude();
//                    double longitude = aMapLocation.getLongitude();
//
//                    // Move camera to current location
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//
//                    // Add a marker at the current location
//                    aMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
//
//                    // Optionally, display a toast with location info
//                    Toast.makeText(MainActivity.this, "Current Location: " + latitude + ", " + longitude, Toast.LENGTH_LONG).show();
//                } else {
//                    // Location failure
//                    Toast.makeText(MainActivity.this, "Location failed, error: " + aMapLocation.getErrorCode(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        // Start location updates
//        locationClient.setLocationOption(locationOption);
//        locationClient.startLocation();
//    }
//    private void showPrivacyDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("隐私政策");
//        builder.setMessage("我们会收集和使用您的位置信息，以提供更精准的服务。请阅读并同意隐私政策后继续使用本应用。");
//        builder.setPositiveButton("同意", (dialog, which) -> {
//            // 用户同意
//            AMapLocationClient.updatePrivacyShow(this, true, true);
//            AMapLocationClient.updatePrivacyAgree(this, true);
//            dialog.dismiss();
//        });
//        builder.setNegativeButton("拒绝", (dialog, which) -> {
//            // 用户拒绝
//            Toast.makeText(this, "您需要同意隐私政策才能使用定位功能", Toast.LENGTH_SHORT).show();
//            finish();
//        });
//        builder.setCancelable(false);
//        builder.show();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Stop location updates when activity is destroyed
//        if (locationClient != null) {
//            locationClient.stopLocation();
//            locationClient.onDestroy();
//        }
//        mapView.onDestroy();
//    }
//}
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