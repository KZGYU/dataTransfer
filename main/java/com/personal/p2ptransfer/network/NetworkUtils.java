package com.personal.p2ptransfer.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    /**
     * 外部APIからグローバルIPアドレスを取得する。
     * @return グローバルIPアドレスを表す文字列
     */
    public static String fetchGlobalIP() {
        String ipServiceURL = "https://api.ipify.org";
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(ipServiceURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "IPアドレスの取得に失敗しました。";
        }
        return result.toString();
    }
}

