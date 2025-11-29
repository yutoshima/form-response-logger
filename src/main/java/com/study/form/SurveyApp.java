package com.study.form;

import com.formdev.flatlaf.FlatLightLaf;
import com.study.form.ui.MainWindow;
import com.study.form.util.ConfigManager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.File;

/**
 * メインアプリケーションクラス
 */
public class SurveyApp {
    
    public static void main(String[] args) {
        // 初期セットアップ
        setupDirectories();
        
        // Look and Feelを設定
        try {
            ConfigManager configManager = new ConfigManager();
            String appearanceMode = configManager.getConfig().getAppearanceMode();
            
            // FlatLafテーマを適用
            FlatLightLaf.setup();
            
            // システムのLook and Feelを使用する場合
            if ("System".equals(appearanceMode)) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // GUIを起動
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }
    
    private static void setupDirectories() {
        // ディレクトリが存在しない場合は作成
        createDirectoryIfNotExists(Constants.DATA_DIR);
        createDirectoryIfNotExists(Constants.QUESTIONS_DIR);
        createDirectoryIfNotExists(Constants.RESPONSES_DIR);
        createDirectoryIfNotExists(Constants.LOGS_DIR);
        
        // サンプル問題ファイルを作成
        createSampleQuestionsFile();
    }
    
    private static void createDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    private static void createSampleQuestionsFile() {
        String samplePath = getSampleFilePath();
        File sampleFile = new File(samplePath);

        if (!sampleFile.exists()) {
            writeSampleQuestionsFile(sampleFile);
        }
    }

    private static String getSampleFilePath() {
        return Constants.QUESTIONS_DIR + File.separator + Constants.DEFAULT_QUESTIONS_FILE;
    }

    private static void writeSampleQuestionsFile(File sampleFile) {
        try (java.io.PrintWriter writer = createUTF8Writer(sampleFile)) {
            writeSampleFileHeader(writer);
            writeSampleFileContent(writer);
        } catch (Exception e) {
            System.err.println("サンプル問題ファイルの作成に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static java.io.PrintWriter createUTF8Writer(File file) throws java.io.IOException {
        return new java.io.PrintWriter(
            new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(file),
                java.nio.charset.StandardCharsets.UTF_8));
    }

    private static void writeSampleFileHeader(java.io.PrintWriter writer) {
        writer.write('\ufeff'); // BOM
        writer.println("問題番号,質問文,選択肢1,選択肢2,選択肢3,選択肢4,選択肢5");
    }

    private static void writeSampleFileContent(java.io.PrintWriter writer) {
        writer.println("1,Javaプログラミングの経験はありますか？,全くない,少しある,ある程度ある,かなりある,");
        writer.println("2,GUIアプリケーション開発の経験はありますか？,全くない,少しある,ある程度ある,かなりある,");
    }
}
