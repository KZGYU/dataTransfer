package com.personal.p2ptransfer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataTransferApplication.class, args);
        createAndShowGUI();
    }

    private static void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Data Transfer Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // 左右のパネルの高さを調整
            int panelWidth = 300; // 適切な幅に設定
            int panelHeight = 400; // 適切な高さに設定

            // 左パネル（自分のフォルダ）
            JPanel leftPanel = new JPanel(new BorderLayout());
            JTextArea textAreaLeft = new JTextArea();
            textAreaLeft.setBorder(BorderFactory.createTitledBorder("自分のフォルダ"));
            leftPanel.add(new JScrollPane(textAreaLeft), BorderLayout.CENTER);
            leftPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
            
         // 参照ボタンの追加
            JButton browseButton = new JButton("参照");
            browseButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    textAreaLeft.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            });

            // 参照ボタンを下部に配置するためのパネル
            JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            browsePanel.add(browseButton);
            leftPanel.add(browsePanel, BorderLayout.SOUTH);

            // 右パネル（相手のフォルダ）
            JPanel rightPanel = new JPanel(new BorderLayout());
            JTextArea textAreaRight = new JTextArea();
            textAreaRight.setBorder(BorderFactory.createTitledBorder("相手のフォルダ"));
            rightPanel.add(new JScrollPane(textAreaRight), BorderLayout.CENTER);
            rightPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

            // 中央のボタンパネル（追加、削除ボタン）
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton addButton = new JButton("追加");
            JButton deleteButton = new JButton("削除");
            centerPanel.add(addButton);
            centerPanel.add(deleteButton);

            // ステータスパネル（グローバルIPアドレス＋再取得ボタン）
            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel ipLabel = new JLabel("グローバルIPアドレス: ");
            JTextField globalIPField = new JTextField(15);
            globalIPField.setEditable(false);
            JButton refreshButton = new JButton("再取得");
            statusPanel.add(ipLabel);
            statusPanel.add(globalIPField);
            statusPanel.add(refreshButton);
            statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 30)); // ステータスパネルの高さを設定

            // データ転送ボタン（右下配置）
            JButton transferButton = new JButton("データ転送");
            JPanel transferPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            transferPanel.add(transferButton);
            
            // ステータスパネルと転送ボタンを一つのパネルに統合
            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.add(statusPanel, BorderLayout.CENTER);
            southPanel.add(transferPanel, BorderLayout.EAST);

            // コンポーネントの配置
            frame.add(leftPanel, BorderLayout.WEST);
            frame.add(centerPanel, BorderLayout.CENTER);
            frame.add(rightPanel, BorderLayout.EAST);
            frame.add(southPanel, BorderLayout.SOUTH);

            // ウィンドウのサイズを設定し、表示する前にパック
            frame.setPreferredSize(new Dimension(1000, 600));
            frame.pack();
            frame.setLocationRelativeTo(null); // 画面の中央に表示
            frame.setVisible(true);
        });
    }
}
