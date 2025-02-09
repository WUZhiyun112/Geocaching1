package com.example.geocaching1;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

//public class LoginActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);  // 确保这里的布局文件名称和 XML 文件一致
//    }
//}


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geocaching1.network.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL = "http://10.0.2.2:8080/api/users/login";  // 你的后端登录接口

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameField = findViewById(R.id.username);
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.button_confirm);

        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // 校验用户输入
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "请填写用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // 调用登录方法
            loginUser(username, password);
        });
    }

    private void loginUser(String username, String password) {
        Log.d(TAG, "Attempting to log in with username: " + username);  // 用户名
        OkHttpClient client = ApiClient.getUnsafeOkHttpClient();  // 获取不验证 SSL 的客户端
        Log.d(TAG, "进行了okhttpclient" );
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            Log.d(TAG, "Request body: " + json.toString());  // 请求体
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error building JSON request body", e);  // 请求体错误
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(LOGIN_URL)  // 登录接口的 URL
                .post(body)
                .build();

        Log.d(TAG, "Sending request to: " + LOGIN_URL);  // 请求的 URL

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed", e);  // 网络请求失败
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "网络错误，请重试！", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Response received, code: " + response.code());  // 响应状态码
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Response body: " + responseBody);  // 响应体

                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        boolean success = responseJson.getBoolean("success");

                        if (success) {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response JSON", e);  // 解析错误
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Login failed with code: " + response.code());  // 登录失败
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "登录失败，请重试！", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

}
