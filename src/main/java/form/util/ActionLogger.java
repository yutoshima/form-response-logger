package form.util;

import form.Constants;
import form.model.Config;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * アクションロガー - ユーザーアクションをログに記録
 */
public class ActionLogger {
    private final String logFile;
    private final Config config;
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public ActionLogger(String logFile, Config config) {
        this.logFile = logFile;
        this.config = config;
        initializeLogFile();
    }
    
    private void initializeLogFile() {
        File file = new File(logFile);
        if (file.exists()) {
            logInfo("既存のログファイルを使用します: " + logFile);
            return;
        }

        createParentDirectory(file);
        createLogFileWithHeader(file);
    }

    /**
     * 親ディレクトリを作成します。
     */
    private void createParentDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean created = parent.mkdirs();
            logInfo("ログディレクトリを作成: " + parent.getAbsolutePath() + " (成功: " + created + ")");
        }
    }

    /**
     * ヘッダー行を含むログファイルを作成します。
     */
    private void createLogFileWithHeader(File file) {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            writer.println("タイムスタンプ,アクション種別,詳細情報");
            logInfo("ログファイルを初期化しました: " + logFile);
        } catch (Exception e) {
            logError("ログファイルの初期化に失敗しました: " + logFile, e);
        }
    }

    /**
     * 情報メッセージをコンソールに出力します。
     */
    private void logInfo(String message) {
        System.out.println(message);
    }

    /**
     * エラーメッセージとスタックトレースをコンソールに出力します。
     */
    private void logError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
    
    private void logAction(String actionType, String details) {
        String logEntry = createLogEntry(actionType, details);
        writeLogEntry(logEntry);
    }

    /**
     * ログエントリを作成します。
     */
    private String createLogEntry(String actionType, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        return timestamp + "," + actionType + "," + escapeCSV(details);
    }

    /**
     * ログエントリをファイルに書き込みます。
     */
    private void writeLogEntry(String logEntry) {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8"))) {
            writer.println(logEntry);
            logInfo("ログ記録: " + logEntry);
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
        logAction(config.getLogActionChoiceSelection(), "問題" + questionNum + ": " + choice);
    }

    public void logReasonStart(int questionNum) {
        logAction(config.getLogActionReasonStart(), "問題" + questionNum);
    }

    public void logReasonText(int questionNum, String reasonText) {
        String preview = reasonText;
        if (reasonText.length() > Constants.LOG_TEXT_PREVIEW_LENGTH) {
            preview = reasonText.substring(0, Constants.LOG_TEXT_PREVIEW_LENGTH) + "...";
        }
        logAction(config.getLogActionReasonText(), "問題" + questionNum + ": " + preview);
    }

    public void logRewriteReason(int questionNum) {
        logAction(config.getLogActionReasonRewrite(), "問題" + questionNum);
    }

    public void logNextQuestion(int fromNum, int toNum) {
        logAction(config.getLogActionQuestionMove(), "問題" + fromNum + " → 問題" + toNum);
    }

    public void logSubmit() {
        logAction(config.getLogActionSubmit(), "完了");
    }
}
