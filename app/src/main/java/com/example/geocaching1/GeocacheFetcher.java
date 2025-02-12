package com.example.geocaching1;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GeocacheFetcher {
    private static final String API_KEY = "baxewkyrs3UzEBF64PY3"; // wo的 Key
    private static final String SECRET_KEY = "yV7kv9ESQbW7TzWsm9B2BmcUQwzMvBMZ898ZkxCk";
    private static final String API_URL = "https://www.opencaching.de/okapi/services/caches/search/nearest";


    public static String fetchGeocaches(double latitude, double longitude) {
        HttpURLConnection conn = null;
        BufferedReader in = null;

        try {
            // 构建请求 URL
            String requestUrl = API_URL + "?consumer_key=" + API_KEY +
                    "&center=" + latitude + "|" + longitude +
                    "&limit=50";

            Log.d("GeocacheFetcher", "Request URL: " + requestUrl);

            // 打开连接
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 设置连接超时（毫秒）
            conn.setReadTimeout(5000);    // 设置读取超时（毫秒）

            // 获取响应码
            int responseCode = conn.getResponseCode();
//            Log.d("GeocacheFetcher", "Response Code: " + responseCode);

            // 检查响应是否成功
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                // 读取响应
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                Log.d("GeocacheFetcher", "API Response: " + response);
                return response.toString(); // 返回响应字符串
            } else {
                Log.e("GeocacheFetcher", "Error: HTTP Response Code " + responseCode);
                return "Error: HTTP Response Code " + responseCode;
            }
        } catch (Exception e) {
            Log.e("GeocacheFetcher", "Exception occurred: " + e.getMessage(), e);
            return "Exception: " + e.getMessage();
        } finally {
            // 确保资源关闭
            try {
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                Log.e("GeocacheFetcher", "Error closing resources: " + e.getMessage(), e);
            }
        }
    }

    public static String fetchGeocacheDetails(String cacheCode) {
        try {
            String requestUrl = "https://www.opencaching.de/okapi/services/caches/geocache" +
                    "?consumer_key=" + API_KEY +
                    "&cache_code=" + cacheCode +
                    "&fields=code|name|location|type|status" + // 添加更多字段
                    "&format=json"; // 确保返回 JSON 格式

//            Log.d("GeocacheDetails", "Request URL: " + requestUrl); // 打印 URL 调试
//            System.out.println("Request URL: " + requestUrl);

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
//            Log.d("GeocacheDetails", "Response Code: " + responseCode); // 打印响应码

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("GeocacheDetails", "Details Response: " + response); // 输出响应结果
                return response.toString();
            } else {
                Log.e("GeocacheDetails", "Error Response Code: " + responseCode);
                return "Error: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GeocacheDetails", "Exception: " + e.getMessage());
            return "Exception: " + e.getMessage();
        }
    }


}
