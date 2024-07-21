package com.personal.p2ptransfer.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.personal.p2ptransfer.network.ConnectionTester;
import com.personal.p2ptransfer.network.FileTransfer;
import com.personal.p2ptransfer.network.NetworkUtils;

public class MainFrame extends JFrame {
    private JTextArea localFolderTextArea;
    private JTextArea remoteFolderTextArea;
    private JTextField globalIPField;
    private JTextField remoteIPField;
    private JLabel testResultLabel;

    public MainFrame() {
        setTitle("Data Transfer Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        int panelWidth = 400; 
        int panelHeight = 400; 

        JPanel leftPanel = createFolderPanel("自分のフォルダ", true);
        leftPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = createFolderPanel("相手のフォルダ", false);
        rightPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        add(rightPanel, BorderLayout.EAST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JButton sendAllButton = createButton("全て送信", e -> {
            String peerIp = remoteIPField.getText();
            FileTransfer.sendFiles(peerIp, localFolderTextArea);
        });
        JButton receiveAllButton = createButton("全て受信", e -> {
            String peerIp = remoteIPField.getText();
            FileTransfer.requestFiles(peerIp, localFolderTextArea);
        });

        centerPanel.add(sendAllButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        centerPanel.add(receiveAllButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        add(centerPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new BorderLayout());

        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel ipLabel = new JLabel("グローバルIPアドレス: ");
        globalIPField = new JTextField(15);
        globalIPField.setEditable(false);
        globalIPField.setText(NetworkUtils.fetchGlobalIP());
        JButton updateIPButton = new JButton("IP更新");
        updateIPButton.addActionListener(e -> {
            globalIPField.setText(NetworkUtils.fetchGlobalIP());
        });
        ipPanel.add(ipLabel);
        ipPanel.add(globalIPField);
        ipPanel.add(updateIPButton);

        JPanel remoteIPPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel remoteIPLabel = new JLabel("相手のグローバルIPアドレス: ");
        remoteIPField = new JTextField(15);
        remoteIPPanel.add(remoteIPLabel);
        remoteIPPanel.add(remoteIPField);

        JPanel testConnectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton testConnectionButton = new JButton("テスト接続");
        testResultLabel = new JLabel("結果: ");
        testConnectionButton.addActionListener(e -> {
            String ip = remoteIPField.getText(); 
            if (ip.isEmpty()) {
                testResultLabel.setText("結果: NG");
            } else {
                int port = 54333; 
                new Thread(() -> {
                    boolean connectionSuccess = ConnectionTester.testConnection(ip, port);
                    SwingUtilities.invokeLater(() -> {
                        testResultLabel.setText("結果: " + (connectionSuccess ? "OK" : "NG"));
                    });
                }).start();
            }
        });
        testConnectionPanel.add(testConnectionButton);
        testConnectionPanel.add(testResultLabel);

        statusPanel.add(ipPanel, BorderLayout.NORTH);
        statusPanel.add(remoteIPPanel, BorderLayout.CENTER);
        statusPanel.add(testConnectionPanel, BorderLayout.SOUTH);

        add(statusPanel, BorderLayout.SOUTH);

        FileTransfer.displayLocalFolderContents(localFolderTextArea);
    }

    private JPanel createFolderPanel(String title, boolean isLocal) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        if (isLocal) {
            localFolderTextArea = textArea; 
            FileTransfer.displayLocalFolderContents(localFolderTextArea); // ローカルフォルダの内容を表示
        } else {
            remoteFolderTextArea = textArea; 
        }

        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, button.getMinimumSize().height));
        button.addActionListener(actionListener);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
            new Thread(FileTransfer::startFileReceiver).start();
        });
    }
}
