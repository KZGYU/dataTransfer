package com.personal.p2ptransfer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;

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
import com.personal.p2ptransfer.network.NetworkUtils;

public class DataTransferApplication {

    private static JTextArea localFolderTextArea;
    private static JTextArea remoteFolderTextArea;
    private static JTextField globalIPField;
    private static JTextField remoteIPField;
    private static JLabel testResultLabel;
    private static final String LOCAL_FOLDER_PATH = "C:\\共有フォルダ"; // 自分のフォルダのパス

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DataTransferApplication::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Data Transfer Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int panelWidth = 400; // パネルの幅
        int panelHeight = 400; // パネルの高さ

        JPanel leftPanel = createFolderPanel("自分のフォルダ", true, frame);
        leftPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        frame.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = createFolderPanel("相手のフォルダ", false, frame);
        rightPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        frame.add(rightPanel, BorderLayout.EAST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JButton sendAllButton = createButton("全て送信", e -> {
            // 全て送信ボタンの処理
        });
        JButton receiveAllButton = createButton("全て受信", e -> {
            // 全て受信ボタンの処理
        });

        centerPanel.add(sendAllButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        centerPanel.add(receiveAllButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        frame.add(centerPanel, BorderLayout.CENTER);

        // ステータスパネルの設定
        JPanel statusPanel = new JPanel(new BorderLayout());

        // 自分のグローバルIPアドレス
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel ipLabel = new JLabel("グローバルIPアドレス: ");
        globalIPField = new JTextField(15);
        globalIPField.setEditable(false);
        globalIPField.setText(getGlobalIP());
        JButton updateIPButton = new JButton("IP更新");
        updateIPButton.addActionListener(e -> {
            globalIPField.setText(getGlobalIP());
        });
        ipPanel.add(ipLabel);
        ipPanel.add(globalIPField);
        ipPanel.add(updateIPButton);

        // 相手のグローバルIP入力フィールド
        JPanel remoteIPPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel remoteIPLabel = new JLabel("相手のグローバルIPアドレス: ");
        remoteIPField = new JTextField(15);
        remoteIPPanel.add(remoteIPLabel);
        remoteIPPanel.add(remoteIPField);

        // テスト接続ボタンと結果表示ラベル
        JPanel testConnectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton testConnectionButton = new JButton("テスト接続");
        testResultLabel = new JLabel("結果: ");
        testConnectionButton.addActionListener(e -> {
            String ip = remoteIPField.getText(); // 相手のIPアドレスを取得
            int port = 48168; // ここに適切なポート番号を入力
            boolean connectionSuccess = ConnectionTester.testConnection(ip, port);
            testResultLabel.setText("結果: " + (connectionSuccess ? "OK" : "NG"));
        });
        testConnectionPanel.add(testConnectionButton);
        testConnectionPanel.add(testResultLabel);

        // データ転送ボタン
        JPanel transferButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton transferButton = new JButton("データ転送");
        transferButton.addActionListener(e -> {
            // データ転送のアクション処理
        });
        transferButtonPanel.add(transferButton);

        // ステータスパネルに各パネルを配置
        statusPanel.add(ipPanel, BorderLayout.NORTH);
        statusPanel.add(remoteIPPanel, BorderLayout.CENTER); // 追加
        statusPanel.add(testConnectionPanel, BorderLayout.SOUTH);
        statusPanel.add(transferButtonPanel, BorderLayout.EAST);

        frame.add(statusPanel, BorderLayout.SOUTH);

        // フレームの設定
        frame.pack();
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // アプリ起動時に自分のフォルダの内容を表示
        displayLocalFolderContents();
    }

    private static void displayLocalFolderContents() {
        File localFolder = new File(LOCAL_FOLDER_PATH);
        if (localFolder.exists() && localFolder.isDirectory()) {
            File[] files = localFolder.listFiles();
            if (files != null) {
                localFolderTextArea.setText(""); // テキストエリアをクリア
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

    private static String getGlobalIP() {
        return NetworkUtils.fetchGlobalIP();
    }

    private static JPanel createFolderPanel(String title, boolean isLocal, JFrame frame) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        if (isLocal) {
            localFolderTextArea = textArea; // 自分のフォルダ用のテキストエリアを保存
            displayLocalFolderContents(); // ローカルフォルダの内容を表示
        }

        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private static JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, button.getMinimumSize().height));
        button.addActionListener(actionListener);
        return button;
    }
}
