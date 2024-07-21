package com.personal.p2ptransfer.network;

import java.io.IOException;
import java.net.Socket;

public class ConnectionTester {
    public static boolean testConnection(String ipAddress, int port) {
        try (Socket socket = new Socket()) {
            System.out.println("接続を試みています: " + ipAddress + ":" + port);
            socket.connect(new java.net.InetSocketAddress(ipAddress, port), 5000); // タイムアウトを5000msに設定
            System.out.println("接続に成功しました: " + ipAddress + ":" + port);
            // サーバーに接続したら、簡単なデータ送受信でテスト
            socket.getOutputStream().write(1);
            if (socket.getInputStream().read() == 1) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("接続に失敗しました: " + ipAddress + ":" + port);
        }
        return false;
    }
}
