package com.example.geocaching1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SelfActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);

        // Setup TextViews
        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);

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
}
