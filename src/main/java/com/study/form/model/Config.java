package com.study.form.model;

import java.util.HashMap;
import java.util.Map;

/**
 * アプリケーション設定データモデル
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
    private int contentWidth;
    private boolean randomizeChoices;
    private boolean enablePrevButton;

    private String buttonCreateQuestions;
    private String buttonTakeSurvey;
    private String buttonNextQuestion;
    private String buttonPrevQuestion;
    private String buttonReselect;
    private String buttonFinishSurvey;

    private String titleMain;
    private String titleQuestionEditor;
    private String titleSettings;
    private String titleSurvey;

    private String logActionChoiceSelection;
    private String logActionReasonStart;
    private String logActionReasonText;
    private String logActionReasonRewrite;
    private String logActionQuestionMove;
    private String logActionSubmit;

    public Config() {
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
        this.contentWidth = 720;
        this.randomizeChoices = false;
        this.enablePrevButton = false;
        this.buttonCreateQuestions = "問題を作成";
        this.buttonTakeSurvey = "アンケートに回答";
        this.buttonNextQuestion = "次の問題へ";
        this.buttonPrevQuestion = "前の問題へ";
        this.buttonReselect = "選択肢を選び直す";
        this.buttonFinishSurvey = "回答を終了する";
        this.titleMain = "研究用アンケートシステム";
        this.titleQuestionEditor = "問題作成";
        this.titleSettings = "設定";
        this.titleSurvey = "アンケート回答";
        this.logActionChoiceSelection = "選択肢選択";
        this.logActionReasonStart = "理由入力開始";
        this.logActionReasonText = "理由入力内容";
        this.logActionReasonRewrite = "理由書き直し";
        this.logActionQuestionMove = "問題移動";
        this.logActionSubmit = "アンケート送信";
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

    public int getContentWidth() {
        return contentWidth > 0 ? contentWidth : 720;
    }

    public void setContentWidth(int contentWidth) {
        this.contentWidth = contentWidth;
    }

    public boolean isRandomizeChoices() {
        return randomizeChoices;
    }

    public void setRandomizeChoices(boolean randomizeChoices) {
        this.randomizeChoices = randomizeChoices;
    }

    public boolean isEnablePrevButton() {
        return enablePrevButton;
    }

    public void setEnablePrevButton(boolean enablePrevButton) {
        this.enablePrevButton = enablePrevButton;
    }

    public String getButtonCreateQuestions() {
        return buttonCreateQuestions != null ? buttonCreateQuestions : "問題を作成";
    }

    public void setButtonCreateQuestions(String buttonCreateQuestions) {
        this.buttonCreateQuestions = buttonCreateQuestions;
    }

    public String getButtonTakeSurvey() {
        return buttonTakeSurvey != null ? buttonTakeSurvey : "アンケートに回答";
    }

    public void setButtonTakeSurvey(String buttonTakeSurvey) {
        this.buttonTakeSurvey = buttonTakeSurvey;
    }

    public String getButtonNextQuestion() {
        return buttonNextQuestion != null ? buttonNextQuestion : "次の問題へ";
    }

    public void setButtonNextQuestion(String buttonNextQuestion) {
        this.buttonNextQuestion = buttonNextQuestion;
    }

    public String getButtonPrevQuestion() {
        return buttonPrevQuestion != null ? buttonPrevQuestion : "前の問題へ";
    }

    public void setButtonPrevQuestion(String buttonPrevQuestion) {
        this.buttonPrevQuestion = buttonPrevQuestion;
    }

    public String getButtonReselect() {
        return buttonReselect != null ? buttonReselect : "選択肢を選び直す";
    }

    public void setButtonReselect(String buttonReselect) {
        this.buttonReselect = buttonReselect;
    }

    public String getButtonFinishSurvey() {
        return buttonFinishSurvey != null ? buttonFinishSurvey : "回答を終了する";
    }

    public void setButtonFinishSurvey(String buttonFinishSurvey) {
        this.buttonFinishSurvey = buttonFinishSurvey;
    }

    public String getTitleMain() {
        return titleMain != null ? titleMain : "研究用アンケートシステム";
    }

    public void setTitleMain(String titleMain) {
        this.titleMain = titleMain;
    }

    public String getTitleQuestionEditor() {
        return titleQuestionEditor != null ? titleQuestionEditor : "問題作成";
    }

    public void setTitleQuestionEditor(String titleQuestionEditor) {
        this.titleQuestionEditor = titleQuestionEditor;
    }

    public String getTitleSettings() {
        return titleSettings != null ? titleSettings : "設定";
    }

    public void setTitleSettings(String titleSettings) {
        this.titleSettings = titleSettings;
    }

    public String getTitleSurvey() {
        return titleSurvey != null ? titleSurvey : "アンケート回答";
    }

    public void setTitleSurvey(String titleSurvey) {
        this.titleSurvey = titleSurvey;
    }

    public String getLogActionChoiceSelection() {
        return logActionChoiceSelection != null ? logActionChoiceSelection : "選択肢選択";
    }

    public void setLogActionChoiceSelection(String logActionChoiceSelection) {
        this.logActionChoiceSelection = logActionChoiceSelection;
    }

    public String getLogActionReasonStart() {
        return logActionReasonStart != null ? logActionReasonStart : "理由入力開始";
    }

    public void setLogActionReasonStart(String logActionReasonStart) {
        this.logActionReasonStart = logActionReasonStart;
    }

    public String getLogActionReasonText() {
        return logActionReasonText != null ? logActionReasonText : "理由入力内容";
    }

    public void setLogActionReasonText(String logActionReasonText) {
        this.logActionReasonText = logActionReasonText;
    }

    public String getLogActionReasonRewrite() {
        return logActionReasonRewrite != null ? logActionReasonRewrite : "理由書き直し";
    }

    public void setLogActionReasonRewrite(String logActionReasonRewrite) {
        this.logActionReasonRewrite = logActionReasonRewrite;
    }

    public String getLogActionQuestionMove() {
        return logActionQuestionMove != null ? logActionQuestionMove : "問題移動";
    }

    public void setLogActionQuestionMove(String logActionQuestionMove) {
        this.logActionQuestionMove = logActionQuestionMove;
    }

    public String getLogActionSubmit() {
        return logActionSubmit != null ? logActionSubmit : "アンケート送信";
    }

    public void setLogActionSubmit(String logActionSubmit) {
        this.logActionSubmit = logActionSubmit;
    }

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
        map.put("content_width", contentWidth);
        map.put("randomize_choices", randomizeChoices);
        map.put("enable_prev_button", enablePrevButton);
        map.put("button_create_questions", buttonCreateQuestions);
        map.put("button_take_survey", buttonTakeSurvey);
        map.put("button_next_question", buttonNextQuestion);
        map.put("button_prev_question", buttonPrevQuestion);
        map.put("button_reselect", buttonReselect);
        map.put("button_finish_survey", buttonFinishSurvey);
        map.put("title_main", titleMain);
        map.put("title_question_editor", titleQuestionEditor);
        map.put("title_settings", titleSettings);
        map.put("title_survey", titleSurvey);
        map.put("log_action_choice_selection", logActionChoiceSelection);
        map.put("log_action_reason_start", logActionReasonStart);
        map.put("log_action_reason_text", logActionReasonText);
        map.put("log_action_reason_rewrite", logActionReasonRewrite);
        map.put("log_action_question_move", logActionQuestionMove);
        map.put("log_action_submit", logActionSubmit);
        return map;
    }

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
        this.defaultChoices = getInt(map, "default_choices", this.defaultChoices);
        this.choiceColumns = getInt(map, "choice_columns", this.choiceColumns);
        this.logSequence = getInt(map, "log_sequence", this.logSequence);
        this.responseSequence = getInt(map, "response_sequence", this.responseSequence);
        this.contentWidth = getInt(map, "content_width", this.contentWidth);
        if (map.containsKey("use_html_rendering"))
            this.useHtmlRendering = (Boolean) map.get("use_html_rendering");
        if (map.containsKey("randomize_choices"))
            this.randomizeChoices = (Boolean) map.get("randomize_choices");
        if (map.containsKey("enable_prev_button"))
            this.enablePrevButton = (Boolean) map.get("enable_prev_button");
        if (map.containsKey("button_create_questions"))
            this.buttonCreateQuestions = (String) map.get("button_create_questions");
        if (map.containsKey("button_take_survey"))
            this.buttonTakeSurvey = (String) map.get("button_take_survey");
        if (map.containsKey("button_next_question"))
            this.buttonNextQuestion = (String) map.get("button_next_question");
        if (map.containsKey("button_prev_question"))
            this.buttonPrevQuestion = (String) map.get("button_prev_question");
        if (map.containsKey("button_reselect"))
            this.buttonReselect = (String) map.get("button_reselect");
        if (map.containsKey("button_finish_survey"))
            this.buttonFinishSurvey = (String) map.get("button_finish_survey");
        if (map.containsKey("title_main"))
            this.titleMain = (String) map.get("title_main");
        if (map.containsKey("title_question_editor"))
            this.titleQuestionEditor = (String) map.get("title_question_editor");
        if (map.containsKey("title_settings"))
            this.titleSettings = (String) map.get("title_settings");
        if (map.containsKey("title_survey"))
            this.titleSurvey = (String) map.get("title_survey");
        if (map.containsKey("log_action_choice_selection"))
            this.logActionChoiceSelection = (String) map.get("log_action_choice_selection");
        if (map.containsKey("log_action_reason_start"))
            this.logActionReasonStart = (String) map.get("log_action_reason_start");
        if (map.containsKey("log_action_reason_text"))
            this.logActionReasonText = (String) map.get("log_action_reason_text");
        if (map.containsKey("log_action_reason_rewrite"))
            this.logActionReasonRewrite = (String) map.get("log_action_reason_rewrite");
        if (map.containsKey("log_action_question_move"))
            this.logActionQuestionMove = (String) map.get("log_action_question_move");
        if (map.containsKey("log_action_submit"))
            this.logActionSubmit = (String) map.get("log_action_submit");
    }

    private int getInt(Map<String, Object> map, String key, int defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof Integer) return (Integer) value;
        return defaultValue;
    }
}
