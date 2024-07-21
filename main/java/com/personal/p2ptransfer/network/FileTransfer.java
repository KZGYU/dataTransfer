package com.personal.p2ptransfer.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class FileTransfer {

    private static final String LOCAL_FOLDER_PATH = "C:\\共有フォルダ"; 

    public static void sendFiles(String ipAddress, JTextArea localFolderTextArea) {
        try (Socket socket = new Socket(ipAddress, 54333);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            File folder = new File(LOCAL_FOLDER_PATH);
            String[] files = folder.list();
            if (files != null) {
                dos.writeInt(files.length); 
                for (String file : files) {
                    File f = new File(folder, file);
                    dos.writeUTF(file); 
                    dos.writeLong(f.length()); 
                    try (FileInputStream fis = new FileInputStream(f)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, read);
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "ファイル送信完了");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ファイル送信エラー: " + e.getMessage());
        }
    }

    public static void requestFiles(String ipAddress, JTextArea localFolderTextArea) {
        try (Socket socket = new Socket(ipAddress, 54334);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            dos.writeUTF("REQUEST_FILES");
            int fileCount = dis.readInt();
            for (int i = 0; i < fileCount; i++) {
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                File file = new File(LOCAL_FOLDER_PATH, fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    long remaining = fileSize;
                    int read;
                    while (remaining > 0 && (read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                        fos.write(buffer, 0, read);
                        remaining -= read;
                    }
                }
            }
            SwingUtilities.invokeLater(() -> displayLocalFolderContents(localFolderTextArea)); 
            JOptionPane.showMessageDialog(null, "ファイル受信完了");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ファイル受信エラー: " + e.getMessage());
        }
    }

    public static void displayLocalFolderContents(JTextArea localFolderTextArea) {
        File localFolder = new File(LOCAL_FOLDER_PATH);
        if (localFolder.exists() && localFolder.isDirectory()) {
            File[] files = localFolder.listFiles();
            if (files != null) {
                localFolderTextArea.setText(""); 
                for (File file : files) {
                    if (file.isFile()) {
                        localFolderTextArea.append(file.getName() + "\n");
                    } else if (file.isDirectory()) {
                        localFolderTextArea.append(file.getName() + "/\n");
                    }
                }
            }
        } else {
            localFolderTextArea.setText("フォルダが存在しないか、ディレクトリではありません。");
        }
    }

    public static void startFileReceiver() {
        try (ServerSocket serverSocket = new ServerSocket(54333);
             ServerSocket fileRequestServer = new ServerSocket(54334)) {
            System.out.println("ファイル受信サーバーが起動しました。");
            while (true) {
                new Thread(() -> {
                    try (Socket socket = serverSocket.accept();
                         DataInputStream dis = new DataInputStream(socket.getInputStream());
                         DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                        System.out.println("クライアント接続を受信しました。");
                        int fileCount = dis.readInt();
                        for (int i = 0; i < fileCount; i++) {
                            String fileName = dis.readUTF();
                            long fileSize = dis.readLong();
                            File file = new File(LOCAL_FOLDER_PATH, fileName);
                            try (FileOutputStream fos = new FileOutputStream(file)) {
                                byte[] buffer = new byte[4096];
                                long remaining = fileSize;
                                int read;
                                while (remaining > 0 && (read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                                    fos.write(buffer, 0, read);
                                    remaining -= read;
                                }
                            }
                        }
                        SwingUtilities.invokeLater(() -> displayLocalFolderContents(null)); 
                        dos.write(1); // 確認のためのデータを送り返す
                        System.out.println("ファイル受信完了。");
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "ファイル受信エラー: " + e.getMessage());
                    }
                }).start();
                new Thread(() -> {
                    try (Socket socket = fileRequestServer.accept();
                         DataInputStream dis = new DataInputStream(socket.getInputStream());
                         DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                        String request = dis.readUTF();
                        if ("REQUEST_FILES".equals(request)) {
                            File folder = new File(LOCAL_FOLDER_PATH);
                            String[] files = folder.list();
                            if (files != null) {
                                dos.writeInt(files.length);
                                for (String file : files) {
                                    File f = new File(folder, file);
                                    dos.writeUTF(file);
                                    dos.writeLong(f.length());
                                    try (FileInputStream fis = new FileInputStream(f)) {
                                        byte[] buffer = new byte[4096];
                                        int read;
                                        while ((read = fis.read(buffer)) != -1) {
                                            dos.write(buffer, 0, read);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "サーバーソケットエラー: " + e.getMessage());
        }
    }
}
