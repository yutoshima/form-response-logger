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
                config = Config.fromMap(map);
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
        // Configのデフォルトコンストラクタがすべてのデフォルト値を設定
        // ただし、ファイルパスなど特定の値をカスタマイズする必要がある場合は、
        // Map経由で設定を構築
        return new Config();
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

    /**
     * Configを新しいインスタンスで置き換えます
     * @param newConfig 新しい設定オブジェクト
     */
    public void setConfig(Config newConfig) {
        this.config = newConfig;
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
            // 新しいFileSettingsを作成してConfigを更新
            var oldFileSettings = config.fileSettings();
            var newFileSettings = new form.model.config.FileSettings(
                oldFileSettings.questionsDirectory(),
                oldFileSettings.questionsFile(),
                oldFileSettings.logDirectory(),
                oldFileSettings.logNameFormat(),
                oldFileSettings.responseDirectory(),
                oldFileSettings.responseNameFormat(),
                oldFileSettings.logSequence(),
                sequence + 1  // responseSequenceをインクリメント
            );
            config = new Config(
                newFileSettings,
                config.participantSettings(),
                config.uiSettings(),
                config.behaviorSettings(),
                config.recordingSettings(),
                config.buttonLabels(),
                config.windowTitles(),
                config.logActionNames()
            );
        } else {
            sequence = config.getLogSequence();
            // 新しいFileSettingsを作成してConfigを更新
            var oldFileSettings = config.fileSettings();
            var newFileSettings = new form.model.config.FileSettings(
                oldFileSettings.questionsDirectory(),
                oldFileSettings.questionsFile(),
                oldFileSettings.logDirectory(),
                oldFileSettings.logNameFormat(),
                oldFileSettings.responseDirectory(),
                oldFileSettings.responseNameFormat(),
                sequence + 1,  // logSequenceをインクリメント
                oldFileSettings.responseSequence()
            );
            config = new Config(
                newFileSettings,
                config.participantSettings(),
                config.uiSettings(),
                config.behaviorSettings(),
                config.recordingSettings(),
                config.buttonLabels(),
                config.windowTitles(),
                config.logActionNames()
            );
        }

        return sequence;
    }
}
