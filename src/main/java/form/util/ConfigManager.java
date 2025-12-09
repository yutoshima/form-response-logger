package form.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import form.model.Config;
import form.Constants;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * アプリケーション設定の管理クラス
 */
public class ConfigManager {
    private static final String CONFIG_PATH = Constants.CONFIG_FILE;
    private Config config;
    private Gson gson;
    
    public ConfigManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        loadConfig();
    }
    
    private void loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> map = gson.fromJson(reader, type);
                config = new Config();
                config.fromMap(map);
            } catch (Exception e) {
                System.err.println("設定ファイルの読み込みまたはパースに失敗しました: " + e.getMessage());
                config = createDefaultConfig();
            }
        } else {
            config = createDefaultConfig();
            saveConfig();
        }
    }
    
    private Config createDefaultConfig() {
        Config defaultConfig = new Config();
        setDefaultFilePaths(defaultConfig);
        setDefaultUISettings(defaultConfig);
        setDefaultBehaviorSettings(defaultConfig);
        return defaultConfig;
    }

    /**
     * デフォルトのファイルパス設定を行います。
     */
    private void setDefaultFilePaths(Config config) {
        config.setQuestionsDirectory(Constants.QUESTIONS_DIR);
        config.setQuestionsFile(Constants.DEFAULT_QUESTIONS_FILE);
        config.setLogDirectory(Constants.LOGS_DIR);
        config.setLogNameFormat("action_log_{respondent_id}_{date}.csv");
        config.setResponseDirectory(Constants.RESPONSES_DIR);
        config.setResponseNameFormat("responses_{respondent_id}_{date}.csv");
    }

    /**
     * デフォルトのUI設定を行います。
     */
    private void setDefaultUISettings(Config config) {
        config.setAppearanceMode("System");
        config.setColorTheme("blue");
        config.setFontSize("medium");
    }

    /**
     * デフォルトの動作設定を行います。
     */
    private void setDefaultBehaviorSettings(Config config) {
        config.setOutputFormat("csv");
        config.setAutoSave(true);
        config.setDefaultChoices(4);
    }
    
    public void saveConfig() {
        try (Writer writer = new FileWriter(CONFIG_PATH)) {
            gson.toJson(config.toMap(), writer);
        } catch (Exception e) {
            System.err.println("設定ファイルの保存またはシリアライズに失敗しました: " + e.getMessage());
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
    
    public String getLogPath(String respondentId) {
        return buildFilePath(config.getLogDirectory(), config.getLogNameFormat(), respondentId);
    }

    public String getResponsePath(String respondentId) {
        return buildFilePath(config.getResponseDirectory(), config.getResponseNameFormat(), respondentId);
    }

    private String buildFilePath(String directory, String format, String respondentId) {
        if (directory == null || format == null) {
            return null;
        }
        return directory + File.separator + formatFilename(format, respondentId);
    }
    
    private String formatFilename(String format, String respondentId) {
        String filename = replacePlaceholders(format, respondentId);
        filename = processSequenceNumber(filename, format);
        return filename;
    }

    /**
     * ファイル名のプレースホルダーを実際の値に置換します。
     */
    private String replacePlaceholders(String format, String respondentId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_PATTERN);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT_PATTERN);

        String participantName = getConfigValueOrEmpty(config.getParticipantName());
        String participantId = getConfigValueOrEmpty(config.getParticipantId());

        return format
            .replace("{date}", now.format(dateFormatter))
            .replace("{time}", now.format(timeFormatter))
            .replace("{respondent_id}", respondentId)
            .replace("{participant_name}", participantName)
            .replace("{participant_id}", participantId);
    }

    /**
     * 設定値を取得し、nullの場合は空文字を返します。
     */
    private String getConfigValueOrEmpty(String value) {
        return value != null ? value : "";
    }

    /**
     * シーケンス番号を処理します。
     */
    private String processSequenceNumber(String filename, String originalFormat) {
        if (!filename.contains("{sequence}")) {
            return filename;
        }

        int sequence = getAndIncrementSequence(originalFormat);
        String result = filename.replace("{sequence}", Constants.SEQUENCE_FORMAT.formatted(sequence));
        saveConfig();
        return result;
    }

    /**
     * シーケンス番号を取得し、インクリメントします。
     */
    private int getAndIncrementSequence(String format) {
        boolean isResponseFile = format.equals(config.getResponseNameFormat());
        int sequence;

        if (isResponseFile) {
            sequence = config.getResponseSequence();
            config.setResponseSequence(sequence + 1);
        } else {
            sequence = config.getLogSequence();
            config.setLogSequence(sequence + 1);
        }

        return sequence;
    }
}
