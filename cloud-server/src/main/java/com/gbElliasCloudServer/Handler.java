package com.gbElliasCloudServer;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Handler implements Runnable {
    private final String serverDir = "server Files";
    private DataInputStream is;
    private DataOutputStream os;

    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client Accepted");
        List<String> files = getFiles(serverDir);
        sendListOfFiles(serverDir);


    }

    private void sendListOfFiles(String serverDir) throws IOException {
        os.writeUTF("#list");
        List<String> files = getFiles(serverDir);
        os.writeInt(files.size());
        for (String file : files) {
            os.writeUTF(file);
        }
        os.flush();
    }

    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);

    }

    @Override
    public void run() {
        byte[] buf = new byte[256];
        try {
            while (true) {
                String command = is.readUTF();
                System.out.println("received" + command);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
