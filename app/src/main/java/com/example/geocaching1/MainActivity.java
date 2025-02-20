package com.example.geocaching1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import java.lang.ref.WeakReference;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;



public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // 加载自定义图标（确保 geo_star.png 放在 res/drawable 文件夹下）
    BitmapDescriptor customIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPrivacyDialog();

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class); // 使用 MainActivity.this 而不是 this
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_dashboard) {
//                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class); // 假设 DashboardActivity 是另一个界面
//                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    Intent intent = new Intent(MainActivity.this, SelfActivity.class); // 假设 SelfActivity 是个人中心界面
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }


        });

        // 初始化地图视图
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 加载自定义图标
        customIcon = BitmapDescriptorFactory.fromResource(R.drawable.geo_star);

        // 检查权限并开始定位
        checkAndRequestLocationPermission();

        // 手动添加北京海淀区的一个标记
        LatLng haidianLatLng = new LatLng(39.95933, 116.29845);
        aMap.addMarker(new MarkerOptions()
                .position(haidianLatLng)
                .title("海淀区")
                .snippet("北京市海淀区")
                .icon(customIcon)); // 使用自定义图标
    }




    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 未授予权限，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 权限已授予，开始定位
            startLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，开始定位
                startLocation();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // 用户选择了“拒绝并不再提示”，引导用户到设置页面手动开启权限
                    new AlertDialog.Builder(this)
                            .setTitle("权限被拒绝")
                            .setMessage("定位权限已被禁用，请前往设置手动开启，以便正常使用地图功能。")
                            .setPositiveButton("去设置", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    // 用户仅仅是拒绝权限，但没有选择“不再提示”
                    Toast.makeText(this, "定位权限被拒绝，部分功能无法使用", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void startLocation() {
        try {
            locationClient = new AMapLocationClient(this);
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setOnceLocation(true); // 只定位一次
            option.setNeedAddress(true);  // 返回地址信息

            locationClient.setLocationOption(option);

            // 设置定位监听
            locationClient.setLocationListener(location -> {
                if (location != null) {
                    if (location.getErrorCode() == 0) {
                        // 成功获取定位
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String address = location.getAddress();

                        // 在地图上移动相机到当前位置
                        LatLng currentLatLng = new LatLng(latitude, longitude);
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                        // 高德默认的蓝色图标
                        aMap.addMarker(new MarkerOptions()
                                .position(currentLatLng)
                                .title("You are here")
                                .snippet("Latitude: " + latitude + ", Longitude: " + longitude)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))); // 默认蓝色标记

                        // 打印位置信息
                        Log.d("LocationInfo", "Latitude: " + latitude + ", Longitude: " + longitude);
                        Log.d("LocationInfo", "Address: " + address);
                        System.out.println("当前定位 - Latitude: " + latitude + ", Longitude: " + longitude);
                        System.out.println("当前定位地址: " + address);

                        // 调用 fetchGeocacheData，使用实际的经纬度
                        fetchGeocacheData(latitude, longitude);
                    } else {
                        // 定位失败，打印错误信息
                        Log.e("LocationError", "定位失败 - Error Code: " + location.getErrorCode() +
                                ", Error Info: " + location.getErrorInfo());
                        System.out.println("定位失败 - Error Code: " + location.getErrorCode() +
                                ", Error Info: " + location.getErrorInfo());
                    }
                } else {
                    Log.e("LocationError", "定位失败，Location 对象为 null");
                    System.out.println("定位失败，Location 对象为 null");
                }
            });

            // 开始定位
            locationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LocationError", "Exception: " + e.getMessage());
        }
    }





    //    private void fetchGeocacheData() {
//        new FetchGeocachesTask(this).execute();
//    }
    private void fetchGeocacheData(double latitude, double longitude) {
        // 使用真实的经纬度请求数据
        new FetchGeocachesTask(this, latitude, longitude).execute();
    }

    private static class FetchGeocachesTask extends AsyncTask<Void, Void, String> {
        private WeakReference<MainActivity> activityReference;
        private double latitude;
        private double longitude;

        // 构造方法，传递经纬度
        FetchGeocachesTask(MainActivity activity, double latitude, double longitude) {
            activityReference = new WeakReference<>(activity);
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 执行网络请求，使用传入的经纬度
            return GeocacheFetcher.fetchGeocaches(latitude, longitude);
        }

        @Override
        protected void onPostExecute(String result) {
            // 获取 Activity 实例
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return; // Activity 已经被销毁，直接返回
            }

            // 更新 UI
            if (result != null && !result.isEmpty()) {
                if (result.startsWith("Error") || result.startsWith("Exception")) {
                    Toast.makeText(activity, "Error fetching geocaches: " + result, Toast.LENGTH_SHORT).show();
                } else {
                    activity.parseAndShowGeocaches(result);
                }
            } else {
                Toast.makeText(activity, "Failed to load geocaches", Toast.LENGTH_SHORT).show();
            }
        }
    }



//    private void parseAndShowGeocaches(String json) {
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray results = jsonObject.getJSONArray("results");
//
//            for (int i = 0; i < results.length(); i++) {
//                String geocacheId = results.getString(i);
//                Log.d("GeocacheFetcher", "Fetching details for ID: " + geocacheId);
//
//                // 使用 FetchGeocacheDetailsTask 异步任务
//                new FetchGeocacheDetailsTask().execute(geocacheId);
//                // 手动添加北京海淀区的一个标记
//                LatLng haidianLatLng = new LatLng(39.95933, 116.29845); // 北京海淀区经纬度
//                aMap.addMarker(new MarkerOptions()
//                        .position(haidianLatLng)
//                        .title("海淀区")
//                        .snippet("手动添加的点")
//                        .icon(customIcon));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("ParseGeocaches", "Error parsing geocache data: " + e.getMessage());
//            Toast.makeText(this, "Error parsing geocache data", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void parseAndShowGeocaches(String json) {
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray results = jsonObject.getJSONArray("results");
//
//            // 加载自定义图标
//            BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.geo_star);
//
//            for (int i = 0; i < results.length(); i++) {
//                String geocacheId = results.getString(i);
//                Log.d("GeocacheFetcher", "Fetching details for ID: " + geocacheId);
//
//                // 异步获取详细信息
//                String geocacheDetails = GeocacheFetcher.fetchGeocacheDetails(geocacheId);
//                if (geocacheDetails.startsWith("Error") || geocacheDetails.startsWith("Exception")) {
//                    Log.e("GeocacheDetails", "Failed to fetch details for ID: " + geocacheId);
//                    continue;
//                }
//
//                // 解析 geocache 详情
//                JSONObject detailObject = new JSONObject(geocacheDetails);
//                String location = detailObject.optString("location", "");
//                String name = detailObject.optString("name", "Unknown");
//
//                if (!location.isEmpty()) {
//                    String[] parts = location.split("\\|");
//                    double latitude = Double.parseDouble(parts[0]);
//                    double longitude = Double.parseDouble(parts[1]);
//
//                    LatLng latLng = new LatLng(latitude, longitude);
//
//                    // 添加带自定义图标的标记到地图上
//                    aMap.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .title(name)
//                            .snippet("Geocache ID: " + geocacheId)
//                            .icon(customIcon));
//                }
//            }
//
//            // 手动添加北京海淀区的一个标记
//            LatLng haidianLatLng = new LatLng(39.95933, 116.29845); // 北京海淀区经纬度
//            aMap.addMarker(new MarkerOptions()
//                    .position(haidianLatLng)
//                    .title("海淀区")
//                    .snippet("手动添加的点")
//                    .icon(customIcon));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("ParseGeocaches", "Error parsing geocache data: " + e.getMessage());
//            Toast.makeText(this, "Error parsing geocache data", Toast.LENGTH_SHORT).show();
//        }
//    }
private void parseAndShowGeocaches(String json) {
    try {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray results = jsonObject.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            String geocacheId = results.getString(i);
            Log.d("GeocacheFetcher", "Fetching details for ID: " + geocacheId);

            // 使用异步任务获取详细信息，避免主线程阻塞
            new FetchGeocacheDetailsTask().execute(geocacheId);
        }

        // 添加北京海淀区的一个标记
        LatLng haidianLatLng = new LatLng(39.95933, 116.29845); // 北京海淀区经纬度
        aMap.addMarker(new MarkerOptions()
                .position(haidianLatLng)
                .title("海淀区")
                .snippet("手动添加的点")
                .icon(customIcon)); // 使用星星图标

    } catch (Exception e) {
        e.printStackTrace();
        Log.e("ParseGeocaches", "Error parsing geocache data: " + e.getMessage());
        Toast.makeText(this, "Error parsing geocache data", Toast.LENGTH_SHORT).show();
    }
}




//    private class FetchGeocacheDetailsTask extends AsyncTask<String, Void, String> {
//        private String geocacheId;
//
//        @Override
//        protected String doInBackground(String... params) {
//            geocacheId = params[0];
//            return GeocacheFetcher.fetchGeocacheDetails(geocacheId);
//        }
//
//        @Override
//        protected void onPostExecute(String geocacheDetails) {
//            if (geocacheDetails == null || geocacheDetails.startsWith("Error") || geocacheDetails.startsWith("Exception")) {
//                Log.e("GeocacheDetails", "Failed to fetch details for ID: " + geocacheId);
//                return;
//            }
//
//            try {
//                JSONObject detailObject = new JSONObject(geocacheDetails);
//                String location = detailObject.optString("location", "");
//                String name = detailObject.optString("name", "Unknown");
//
//                if (!location.isEmpty()) {
//                    String[] parts = location.split("\\|");
//                    double latitude = Double.parseDouble(parts[0]);
//                    double longitude = Double.parseDouble(parts[1]);
//
//                    LatLng latLng = new LatLng(latitude, longitude);
//
//                    // 在地图上添加标记
//                    aMap.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .title(name)
//                            .snippet("Geocache ID: " + geocacheId)
//                            .icon(customIcon)); // 使用自定义图标
//                }
//            } catch (Exception e) {
//                Log.e("GeocacheDetails", "Error parsing details: " + e.getMessage());
//            }
//        }
//    }
//

    private class FetchGeocacheDetailsTask extends AsyncTask<String, Void, String> {
        private String geocacheId;

        @Override
        protected String doInBackground(String... params) {
            geocacheId = params[0];
            return GeocacheFetcher.fetchGeocacheDetails(geocacheId);
        }

        @Override
        protected void onPostExecute(String geocacheDetails) {
            if (geocacheDetails == null || geocacheDetails.startsWith("Error") || geocacheDetails.startsWith("Exception")) {
                Log.e("GeocacheDetails", "Failed to fetch details for ID: " + geocacheId);
                return;
            }

            try {
                JSONObject detailObject = new JSONObject(geocacheDetails);
                String location = detailObject.optString("location", "");
                String name = detailObject.optString("name", "Unknown");

                if (!location.isEmpty()) {
                    String[] parts = location.split("\\|");
                    double latitude = Double.parseDouble(parts[0]);
                    double longitude = Double.parseDouble(parts[1]);

                    LatLng latLng = new LatLng(latitude, longitude);

                    // 使用星星图标添加标记
                    aMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(name)
                            .snippet("Geocache ID: " + geocacheId)
                            .icon(customIcon)); // 使用自定义图标
                }
            } catch (Exception e) {
                Log.e("GeocacheDetails", "Error parsing details: " + e.getMessage());
            }
        }
    }


    private void showPrivacyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Privacy Policy");
        builder.setMessage("We collect and use your location information to provide more accurate services. Please read and agree to the privacy policy to continue using the app.");
        builder.setPositiveButton("Agree", (dialog, which) -> {
            AMapLocationClient.updatePrivacyShow(this, true, true);
            AMapLocationClient.updatePrivacyAgree(this, true);
            dialog.dismiss();
        });
        builder.setNegativeButton("Disagree", (dialog, which) -> {
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
