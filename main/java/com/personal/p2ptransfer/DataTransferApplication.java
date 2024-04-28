package com.personal.p2ptransfer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
    private static JLabel testResultLabel;

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

        JLabel ipLabel = new JLabel("グローバルIPアドレス: ");
        globalIPField = new JTextField(15);
        globalIPField.setEditable(false);
        globalIPField.setText(getGlobalIP());

        JButton updateIPButton = new JButton("IP更新");
        updateIPButton.addActionListener(e -> {
//            globalIPField.setText(getGlobalIP());
        	globalIPField.setText("133.206.128.192");//ローカルテスト用
        });

        JButton testConnectionButton = new JButton("テスト接続");
        testResultLabel = new JLabel("結果: ");
        testConnectionButton.addActionListener(e -> {
            String ip = globalIPField.getText(); // グローバルIPフィールドからIPアドレスを取得
            int port = 48168; // ここに適切なポート番号を入力
            boolean connectionSuccess = ConnectionTester.testConnection(ip, port);
            testResultLabel.setText("結果: " + (connectionSuccess ? "OK" : "NG"));
        });

        JButton transferButton = new JButton("データ転送");
        transferButton.addActionListener(e -> {
            // データ転送のアクション処理
        });

        // グローバルIPと更新ボタンを含むパネル
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ipPanel.add(ipLabel);
        ipPanel.add(globalIPField);
        ipPanel.add(updateIPButton);

        // テスト接続ボタンと結果表示ラベルを含むパネル
        JPanel testConnectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        testConnectionPanel.add(testConnectionButton);
        testConnectionPanel.add(testResultLabel);

        // データ転送ボタンのみを含むパネル（右揃え）
        JPanel transferButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        transferButtonPanel.add(transferButton);

        // ステータスパネルに各パネルを配置
        statusPanel.add(ipPanel, BorderLayout.WEST);
        statusPanel.add(testConnectionPanel, BorderLayout.CENTER);
        statusPanel.add(transferButtonPanel, BorderLayout.EAST);

        frame.add(statusPanel, BorderLayout.SOUTH);

        // フレームの設定
        frame.pack();
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String getGlobalIP() {
    	return NetworkUtils.fetchGlobalIP();
    }


    private static JPanel createFolderPanel(String title, boolean isLocal, JFrame frame) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 相手のフォルダパス入力欄の追加
        if (!isLocal) {
            JPanel pathInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel pathInputLabel = new JLabel("相手のフォルダパス:");
            JTextField pathInputField = new JTextField(20);
            pathInputField.addActionListener(e -> remoteFolderTextArea.setText(pathInputField.getText()));
            pathInputPanel.add(pathInputLabel);
            pathInputPanel.add(pathInputField);

            panel.add(pathInputPanel, BorderLayout.NORTH);
        } else {
            // 自分のフォルダのための参照ボタンを追加
            JButton browseButton = new JButton("参照");
            browseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            browseButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile();
                    textArea.setText(""); // テキストエリアをクリア
                    File[] files = selectedDirectory.listFiles(); // ディレクトリ内のファイルリストを取得
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile()) {
                                textArea.append(file.getName() + "\n");
                            } else if (file.isDirectory()) { 
                                textArea.append(file.getName() + "/\n"); 
                            }
                        }
                    }
                }

            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.add(Box.createVerticalGlue());
            buttonPanel.add(browseButton);
            buttonPanel.add(Box.createVerticalGlue());

            panel.add(buttonPanel, BorderLayout.SOUTH);
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
