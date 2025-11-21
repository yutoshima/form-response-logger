package com.study.form.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.study.form.model.Config;
import com.study.form.Constants;

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
        return defaultConfig;
    }
    
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
    
    public String getLogPath(String respondentId) {
        return formatFilePath(config.getLogDirectory(), config.getLogNameFormat(), respondentId);
    }

    public String getResponsePath(String respondentId) {
        return formatFilePath(config.getResponseDirectory(), config.getResponseNameFormat(), respondentId);
    }

    private String formatFilePath(String directory, String format, String respondentId) {
        if (directory == null || format == null) {
            return null;
        }

        String filename = formatFilename(format, respondentId);
        return directory + File.separator + filename;
    }
    
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
            saveConfig();
        }

        return filename;
    }
}
