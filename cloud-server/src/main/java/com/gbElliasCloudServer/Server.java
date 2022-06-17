package com.gbElliasCloudServer;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    public static void main(String[] args) throws IOException {
        System.out.println("Server started");
        try (ServerSocket server = new ServerSocket(8189)) {
            while (true) {
                Socket socket = server.accept();
                Handler handler = new Handler(socket);
                new Thread(handler).start();
            }
        }
    }
}


