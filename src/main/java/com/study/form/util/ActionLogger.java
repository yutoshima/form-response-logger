package com.study.form.util;

import com.study.form.Constants;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * アクションロガー - ユーザーアクションをログに記録
 */
public class ActionLogger {
    private String logFile;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public ActionLogger(String logFile) {
        this.logFile = logFile;
        initializeLogFile();
    }
    
    private void initializeLogFile() {
        File file = new File(logFile);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean created = parent.mkdirs();
                System.out.println("ログディレクトリを作成: " + parent.getAbsolutePath() + " (成功: " + created + ")");
            }

            try (PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                writer.println("タイムスタンプ,アクション種別,詳細情報");
                System.out.println("ログファイルを初期化しました: " + logFile);
            } catch (Exception e) {
                System.err.println("ログファイルの初期化に失敗しました: " + logFile);
                e.printStackTrace();
            }
        } else {
            System.out.println("既存のログファイルを使用します: " + logFile);
        }
    }
    
    private void logAction(String actionType, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String logEntry = timestamp + "," + actionType + "," + escapeCSV(details);

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8"))) {
            writer.println(logEntry);
            System.out.println("ログ記録: " + logEntry);
        } catch (Exception e) {
            System.err.println("ログの書き込みに失敗しました: " + logFile);
            System.err.println("ログ内容: " + logEntry);
            e.printStackTrace();
        }
    }
    
    private String escapeCSV(String text) {
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
    
    public void logChoiceSelection(int questionNum, String choice) {
        logAction(Constants.LOG_ACTION_CHOICE_SELECTION, "問題" + questionNum + ": " + choice);
    }
    
    public void logReasonStart(int questionNum) {
        logAction(Constants.LOG_ACTION_REASON_START, "問題" + questionNum);
    }
    
    public void logReasonText(int questionNum, String reasonText) {
        String preview = reasonText;
        if (reasonText.length() > Constants.LOG_TEXT_PREVIEW_LENGTH) {
            preview = reasonText.substring(0, Constants.LOG_TEXT_PREVIEW_LENGTH) + "...";
        }
        logAction(Constants.LOG_ACTION_REASON_TEXT, "問題" + questionNum + ": " + preview);
    }
    
    public void logRewriteReason(int questionNum) {
        logAction(Constants.LOG_ACTION_REASON_REWRITE, "問題" + questionNum);
    }
    
    public void logNextQuestion(int fromNum, int toNum) {
        logAction(Constants.LOG_ACTION_QUESTION_MOVE, "問題" + fromNum + " → 問題" + toNum);
    }
    
    public void logSubmit() {
        logAction(Constants.LOG_ACTION_SUBMIT, "完了");
    }
}
