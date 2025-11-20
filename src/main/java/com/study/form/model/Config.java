package com.study.form.model;

import java.util.HashMap;
import java.util.Map;

/**
 * アプリケーション設定データモデル
 *
 * <p>このクラスは、アンケートアプリケーションの全ての設定情報を保持します。
 * 設定はJSON形式で{@code config.json}ファイルに永続化されます。</p>
 *
 * <p>主な設定項目：</p>
 * <ul>
 *   <li>ファイルパス設定（問題、ログ、回答ファイル）</li>
 *   <li>被験者情報（名前、ID）</li>
 *   <li>UI設定（外観、フォントサイズ、選択肢の列数）</li>
 *   <li>データ設定（出力形式、デフォルト選択肢数）</li>
 *   <li>連番管理（ログ連番、回答連番）</li>
 * </ul>
 *
 * @author Survey App Development Team
 * @version 1.0
 * @since 1.0
 */
public class Config {
    private String questionsDirectory;
    private String questionsFile;
    private String logDirectory;
    private String logNameFormat;
    private String responseDirectory;
    private String responseNameFormat;
    private String participantName;
    private String participantId;
    private String appearanceMode;
    private String colorTheme;
    private String outputFormat;
    private String fontSize;
    private boolean autoSave;
    private boolean useParticipantInfo;
    private int defaultChoices;
    private int choiceColumns;
    private int logSequence;
    private int responseSequence;
    private boolean useHtmlRendering;

    public Config() {
        // デフォルト値
        this.appearanceMode = "System";
        this.colorTheme = "blue";
        this.outputFormat = "csv";
        this.fontSize = "medium";
        this.autoSave = true;
        this.useParticipantInfo = true;
        this.defaultChoices = 4;
        this.choiceColumns = 2;
        this.logSequence = 1;
        this.responseSequence = 1;
        this.useHtmlRendering = false;
    }
    
    public String getQuestionsDirectory() {
        return questionsDirectory;
    }
    
    public void setQuestionsDirectory(String questionsDirectory) {
        this.questionsDirectory = questionsDirectory;
    }
    
    public String getQuestionsFile() {
        return questionsFile;
    }
    
    public void setQuestionsFile(String questionsFile) {
        this.questionsFile = questionsFile;
    }
    
    public String getLogDirectory() {
        return logDirectory;
    }
    
    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }
    
    public String getLogNameFormat() {
        return logNameFormat;
    }
    
    public void setLogNameFormat(String logNameFormat) {
        this.logNameFormat = logNameFormat;
    }
    
    public String getResponseDirectory() {
        return responseDirectory;
    }
    
    public void setResponseDirectory(String responseDirectory) {
        this.responseDirectory = responseDirectory;
    }
    
    public String getResponseNameFormat() {
        return responseNameFormat;
    }
    
    public void setResponseNameFormat(String responseNameFormat) {
        this.responseNameFormat = responseNameFormat;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getAppearanceMode() {
        return appearanceMode;
    }
    
    public void setAppearanceMode(String appearanceMode) {
        this.appearanceMode = appearanceMode;
    }
    
    public String getColorTheme() {
        return colorTheme;
    }
    
    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }
    
    public String getOutputFormat() {
        return outputFormat;
    }
    
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
    
    public String getFontSize() {
        return fontSize;
    }
    
    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }
    
    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean isUseParticipantInfo() {
        return useParticipantInfo;
    }

    public void setUseParticipantInfo(boolean useParticipantInfo) {
        this.useParticipantInfo = useParticipantInfo;
    }

    public int getDefaultChoices() {
        return defaultChoices;
    }

    public void setDefaultChoices(int defaultChoices) {
        this.defaultChoices = defaultChoices;
    }

    public int getChoiceColumns() {
        return choiceColumns;
    }

    public void setChoiceColumns(int choiceColumns) {
        this.choiceColumns = choiceColumns;
    }

    public int getLogSequence() {
        return logSequence;
    }

    public void setLogSequence(int logSequence) {
        this.logSequence = logSequence;
    }

    public int getResponseSequence() {
        return responseSequence;
    }

    public void setResponseSequence(int responseSequence) {
        this.responseSequence = responseSequence;
    }

    public boolean isUseHtmlRendering() {
        return useHtmlRendering;
    }

    public void setUseHtmlRendering(boolean useHtmlRendering) {
        this.useHtmlRendering = useHtmlRendering;
    }

    /**
     * 設定オブジェクトをMapに変換します。
     *
     * <p>このメソッドは、設定をJSON形式でファイルに保存する際に使用されます。
     * 全てのフィールドがスネークケースのキーでMapに格納されます。</p>
     *
     * @return 設定データを含むMap（キーはスネークケース）
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("questions_directory", questionsDirectory);
        map.put("questions_file", questionsFile);
        map.put("log_directory", logDirectory);
        map.put("log_name_format", logNameFormat);
        map.put("response_directory", responseDirectory);
        map.put("response_name_format", responseNameFormat);
        map.put("participant_name", participantName);
        map.put("participant_id", participantId);
        map.put("appearance_mode", appearanceMode);
        map.put("color_theme", colorTheme);
        map.put("output_format", outputFormat);
        map.put("font_size", fontSize);
        map.put("auto_save", autoSave);
        map.put("use_participant_info", useParticipantInfo);
        map.put("default_choices", defaultChoices);
        map.put("choice_columns", choiceColumns);
        map.put("log_sequence", logSequence);
        map.put("response_sequence", responseSequence);
        map.put("use_html_rendering", useHtmlRendering);
        return map;
    }

    /**
     * Mapから設定オブジェクトにデータを読み込みます。
     *
     * <p>このメソッドは、JSON形式の設定ファイルから読み込んだデータを
     * このオブジェクトに反映する際に使用されます。
     * 存在しないキーは無視され、既存のデフォルト値が維持されます。</p>
     *
     * <p>数値型（default_choices、choice_columns、log_sequence、response_sequence）は
     * DoubleとIntegerの両方の型に対応しています。</p>
     *
     * @param map 設定データを含むMap（JSONから読み込まれた形式）
     */
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("questions_directory"))
            this.questionsDirectory = (String) map.get("questions_directory");
        if (map.containsKey("questions_file"))
            this.questionsFile = (String) map.get("questions_file");
        if (map.containsKey("log_directory"))
            this.logDirectory = (String) map.get("log_directory");
        if (map.containsKey("log_name_format"))
            this.logNameFormat = (String) map.get("log_name_format");
        if (map.containsKey("response_directory"))
            this.responseDirectory = (String) map.get("response_directory");
        if (map.containsKey("response_name_format"))
            this.responseNameFormat = (String) map.get("response_name_format");
        if (map.containsKey("participant_name"))
            this.participantName = (String) map.get("participant_name");
        if (map.containsKey("participant_id"))
            this.participantId = (String) map.get("participant_id");
        if (map.containsKey("appearance_mode"))
            this.appearanceMode = (String) map.get("appearance_mode");
        if (map.containsKey("color_theme"))
            this.colorTheme = (String) map.get("color_theme");
        if (map.containsKey("output_format"))
            this.outputFormat = (String) map.get("output_format");
        if (map.containsKey("font_size"))
            this.fontSize = (String) map.get("font_size");
        if (map.containsKey("auto_save"))
            this.autoSave = (Boolean) map.get("auto_save");
        if (map.containsKey("use_participant_info"))
            this.useParticipantInfo = (Boolean) map.get("use_participant_info");
        if (map.containsKey("default_choices")) {
            Object value = map.get("default_choices");
            if (value instanceof Double) {
                this.defaultChoices = ((Double) value).intValue();
            } else if (value instanceof Integer) {
                this.defaultChoices = (Integer) value;
            }
        }
        if (map.containsKey("choice_columns")) {
            Object value = map.get("choice_columns");
            if (value instanceof Double) {
                this.choiceColumns = ((Double) value).intValue();
            } else if (value instanceof Integer) {
                this.choiceColumns = (Integer) value;
            }
        }
        if (map.containsKey("log_sequence")) {
            Object value = map.get("log_sequence");
            if (value instanceof Double) {
                this.logSequence = ((Double) value).intValue();
            } else if (value instanceof Integer) {
                this.logSequence = (Integer) value;
            }
        }
        if (map.containsKey("response_sequence")) {
            Object value = map.get("response_sequence");
            if (value instanceof Double) {
                this.responseSequence = ((Double) value).intValue();
            } else if (value instanceof Integer) {
                this.responseSequence = (Integer) value;
            }
        }
        if (map.containsKey("use_html_rendering"))
            this.useHtmlRendering = (Boolean) map.get("use_html_rendering");
    }
}
