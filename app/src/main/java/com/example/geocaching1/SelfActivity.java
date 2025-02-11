package com.example.geocaching1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.backend.dto.UserResponse;
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


        fetchUserDetails();
        // Set the text from Intent extras
        String username = getIntent().getStringExtra("USERNAME");
        String email = getIntent().getStringExtra("EMAIL");
        usernameTextView.setText("Username: " + username);
        emailTextView.setText("Email: " + email);

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


        // 创建请求
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/users/details")
                .build();

        // 异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 在这里处理请求失败的情况
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(SelfActivity.this, "请求错误: " + response.code(), Toast.LENGTH_LONG).show());
                    return;
                } else {
                    // 解析响应内容
                    String responseData = response.body().string();
                    // 将响应字符串转换为UserResponse对象
                    UserResponse userResponse = new Gson().fromJson(responseData, UserResponse.class);

                    // 回到主线程更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView usernameTextView = findViewById(R.id.usernameTextView);
                            TextView emailTextView = findViewById(R.id.emailTextView);
                            usernameTextView.setText("用户名: " + userResponse.getUsername());
                            emailTextView.setText("邮箱: " + userResponse.getEmail());
                        }
                    });
                }
            }
        });
    }


}
