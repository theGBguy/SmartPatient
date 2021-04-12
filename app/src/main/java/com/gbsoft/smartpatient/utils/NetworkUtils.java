package com.gbsoft.smartpatient.utils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetworkUtils {
    public static boolean isInternetAvailable() {
        try {
            Future<Boolean> future = Executors.newSingleThreadExecutor().submit(() -> {
                int timeOutMillis = 1500;
                Socket socket = new Socket();
                SocketAddress address = new InetSocketAddress("8.8.8.8", 53);
                socket.connect(address, timeOutMillis);
                socket.close();
                return true;
            });
            return future.get();
        } catch (Exception e) {
            return false;
        }
    }
}
