package com.personal.p2ptransfer;

import javax.swing.JFrame;
import javax.swing.JLabel;
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
        // 安全のため、GUI作成はイベントディスパッチスレッドで行う
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // フレームの作成
                JFrame frame = new JFrame("Data Transfer Application");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // レイアウトを定義
                // ここにSwingコンポーネントを配置するコードを書く
                JLabel label = new JLabel("Welcome to Data Transfer Application!");
                frame.getContentPane().add(label);

                // サイズを設定
                frame.setSize(800, 600);

                // フレームを表示
                frame.setVisible(true);
            }
        });
    }
}
