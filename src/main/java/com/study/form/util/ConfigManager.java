package com.study.form.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.study.form.model.Config;
import com.study.form.Constants;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * アプリケーション設定の管理クラス
 *
 * <p>このクラスは、設定ファイル（config.json）の読み込み、保存、および
 * ファイルパスの生成を担当します。</p>
 *
 * <p>主な機能：</p>
 * <ul>
 *   <li>設定ファイルの読み込みと保存</li>
 *   <li>デフォルト設定の生成</li>
 *   <li>ファイル名のテンプレート置換（日付、時刻、被験者情報、連番）</li>
 *   <li>ログファイルと回答ファイルのパス生成</li>
 *   <li>連番の自動インクリメント</li>
 * </ul>
 *
 * @author Survey App Development Team
 * @version 1.0
 * @since 1.0
 */
public class ConfigManager {
    private static final String CONFIG_PATH = Constants.CONFIG_FILE;
    private Config config;
    private Gson gson;
    
    public ConfigManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        loadConfig();
    }
    
    /**
     * 設定ファイルを読み込みます。
     *
     * <p>設定ファイルが存在しない場合、または読み込みに失敗した場合は、
     * デフォルト設定を生成します。</p>
     */
    private void loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> map = gson.fromJson(reader, type);
                config = new Config();
                config.fromMap(map);
            } catch (IOException e) {
                System.err.println("設定ファイルの読み込みに失敗しました: " + e.getMessage());
                config = createDefaultConfig();
            } catch (Exception e) {
                System.err.println("設定ファイルのパースに失敗しました: " + e.getMessage());
                config = createDefaultConfig();
            }
        } else {
            config = createDefaultConfig();
            saveConfig();
        }
    }
    
    private Config createDefaultConfig() {
        Config defaultConfig = new Config();
        defaultConfig.setQuestionsDirectory(Constants.QUESTIONS_DIR);
        defaultConfig.setQuestionsFile(Constants.DEFAULT_QUESTIONS_FILE);
        defaultConfig.setLogDirectory(Constants.LOGS_DIR);
        defaultConfig.setLogNameFormat("action_log_{respondent_id}_{date}.csv");
        defaultConfig.setResponseDirectory(Constants.RESPONSES_DIR);
        defaultConfig.setResponseNameFormat("responses_{respondent_id}_{date}.csv");
        defaultConfig.setAppearanceMode("System");
        defaultConfig.setColorTheme("blue");
        defaultConfig.setOutputFormat("csv");
        defaultConfig.setFontSize("medium");
        defaultConfig.setAutoSave(true);
        defaultConfig.setDefaultChoices(4);
        // ボタン文言はConfig()コンストラクタでデフォルト値が設定済み
        return defaultConfig;
    }
    
    /**
     * 設定をファイルに保存します。
     *
     * <p>保存に失敗した場合は、エラーメッセージを標準エラー出力に表示します。</p>
     */
    public void saveConfig() {
        try (Writer writer = new FileWriter(CONFIG_PATH)) {
            gson.toJson(config.toMap(), writer);
        } catch (IOException e) {
            System.err.println("設定ファイルの保存に失敗しました: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("設定のシリアライズに失敗しました: " + e.getMessage());
        }
    }
    
    public Config getConfig() {
        return config;
    }
    
    public String getQuestionsPath() {
        if (config.getQuestionsDirectory() != null && config.getQuestionsFile() != null) {
            return config.getQuestionsDirectory() + File.separator + config.getQuestionsFile();
        }
        return null;
    }
    
    /**
     * ログファイルのパスを取得します。
     *
     * @param respondentId 回答者ID
     * @return ログファイルのパス、設定が不完全な場合はnull
     * @throws IllegalArgumentException respondentIdがnullの場合
     */
    public String getLogPath(String respondentId) {
        if (respondentId == null) {
            throw new IllegalArgumentException("respondentIdがnullです");
        }
        return formatFilePath(config.getLogDirectory(), config.getLogNameFormat(), respondentId);
    }

    /**
     * 回答ファイルのパスを取得します。
     *
     * @param respondentId 回答者ID
     * @return 回答ファイルのパス、設定が不完全な場合はnull
     * @throws IllegalArgumentException respondentIdがnullの場合
     */
    public String getResponsePath(String respondentId) {
        if (respondentId == null) {
            throw new IllegalArgumentException("respondentIdがnullです");
        }
        return formatFilePath(config.getResponseDirectory(), config.getResponseNameFormat(), respondentId);
    }

    /**
     * ディレクトリとファイル名フォーマットから完全なファイルパスを生成します。
     *
     * @param directory ディレクトリパス
     * @param format ファイル名フォーマット
     * @param respondentId 回答者ID
     * @return 完全なファイルパス、directoryまたはformatがnullの場合はnull
     */
    private String formatFilePath(String directory, String format, String respondentId) {
        if (directory == null || format == null) {
            return null;
        }

        String filename = formatFilename(format, respondentId);
        return directory + File.separator + filename;
    }
    
    /**
     * ファイル名フォーマットテンプレートを実際のファイル名に変換します。
     *
     * <p>このメソッドは、設定で指定されたファイル名フォーマット（テンプレート）を
     * 実際のファイル名に変換します。以下の変数が使用可能です：</p>
     *
     * <ul>
     *   <li>{@code {date}} - 現在日付（yyyyMMdd形式）</li>
     *   <li>{@code {time}} - 現在時刻（HHmmss形式）</li>
     *   <li>{@code {respondent_id}} - 回答者ID（自動生成される8桁のUUID）</li>
     *   <li>{@code {participant_name}} - 被験者名（設定で入力された名前）</li>
     *   <li>{@code {participant_id}} - 被験者ID（設定で入力されたID）</li>
     *   <li>{@code {sequence}} - 連番（3桁の0埋め形式、例: 001, 002）</li>
     * </ul>
     *
     * <p>連番機能について：</p>
     * <ul>
     *   <li>ログファイルと回答ファイルで独立した連番を管理</li>
     *   <li>連番使用時は自動的にインクリメントされ、設定ファイルに保存</li>
     *   <li>3桁の0埋め形式で出力（001, 002, ..., 999）</li>
     * </ul>
     *
     * @param format ファイル名フォーマット（例: "log_{date}_{sequence}.csv"）
     * @param respondentId 回答者ID
     * @return 変換後のファイル名
     */
    private String formatFilename(String format, String respondentId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        LocalDateTime now = LocalDateTime.now();

        String participantName = config.getParticipantName() != null ? config.getParticipantName() : "";
        String participantId = config.getParticipantId() != null ? config.getParticipantId() : "";

        String filename = format
            .replace("{date}", now.format(dateFormatter))
            .replace("{time}", now.format(timeFormatter))
            .replace("{respondent_id}", respondentId)
            .replace("{participant_name}", participantName)
            .replace("{participant_id}", participantId);

        // 連番処理
        if (filename.contains("{sequence}")) {
            boolean isResponseFile = format.equals(config.getResponseNameFormat());
            int sequence;

            if (isResponseFile) {
                sequence = config.getResponseSequence();
                config.setResponseSequence(sequence + 1);
            } else {
                sequence = config.getLogSequence();
                config.setLogSequence(sequence + 1);
            }

            filename = filename.replace("{sequence}", String.format("%03d", sequence));
            saveConfig(); // 連番をインクリメントした後、保存
        }

        return filename;
    }
    
    private int getNextSequenceNumber(String directory, String filenameFormat) {
        File dir = new File(directory);
        if (!dir.exists()) {
            return 1;
        }
        
        String pattern = buildSequencePattern(filenameFormat);
        
        int maxNum = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                int num = extractSequenceNumber(file.getName(), pattern);
                maxNum = Math.max(maxNum, num);
            }
        }
        
        return maxNum + 1;
    }
    
    private String buildSequencePattern(String filenameFormat) {
        return filenameFormat
            .replace(".", "\\.")
            .replace("{sequence}", "(\\d+)")
            .replace("{date}", "\\d{8}")
            .replace("{time}", "\\d{6}")
            .replace("{respondent_id}", "[^_]+")
            .replace("{participant_name}", "[^_]+")
            .replace("{participant_id}", "[^_]+");
    }
    
    private int extractSequenceNumber(String filename, String pattern) {
        if (!filename.matches(pattern)) {
            return 0;
        }
        
        try {
            String numStr = filename.replaceAll(pattern, "$1");
            return Integer.parseInt(numStr);
        } catch (Exception e) {
            return 0;
        }
    }
}
