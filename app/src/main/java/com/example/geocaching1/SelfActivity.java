package com.example.geocaching1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SelfActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);

        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);

        updateUIFromPreferences();

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(SelfActivity.this, MainActivity.class); // 使用 MainActivity.this 而不是 this
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_dashboard) {
//                    Intent intent = new Intent(SelfActivity.this, DashboardActivity.class); // 假设 DashboardActivity 是另一个界面
//                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    Intent intent = new Intent(SelfActivity.this, SelfActivity.class); // 假设 SelfActivity 是个人中心界面
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void fetchUserDetails() {
        OkHttpClient client = new OkHttpClient();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = prefs.getString("JWT_TOKEN", null);
        if (token == null) {
            Toast.makeText(this, "未登录或会话已过期，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/users/details")
                .header("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 在这里处理请求失败的情况
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 打印完整的响应体到日志
                    final String responseBody = response.body().string();
                    Log.e("HTTP_ERROR", "请求错误: " + response.code() + ", 响应体: " + responseBody);
                    runOnUiThread(() -> Toast.makeText(SelfActivity.this, "请求错误: " + response.code(), Toast.LENGTH_LONG).show());
                    return;
                }

                // 处理成功的响应
                runOnUiThread(() -> {
                    // 更新UI逻辑
                });
            }
        });

    }

    private void updateUIFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String username = prefs.getString("USERNAME", "N/A");
        String email = prefs.getString("EMAIL", "N/A");

        usernameTextView.setText("Username: " + username);
        emailTextView.setText("Email: " + email);
    }

    private static class UserResponse {
        private boolean success;
        private String message;
        private String username;
        private String email;

        public UserResponse(boolean success, String message, String username, String email) {
            this.success = success;
            this.message = message;
            this.username = username;
            this.email = email;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }
}
