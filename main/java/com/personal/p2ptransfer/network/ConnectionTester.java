package com.personal.p2ptransfer.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionTester {

    public static boolean testConnection(String ip, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 10000); // 3秒でタイムアウト
            return socket.isConnected();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
