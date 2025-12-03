package form.model;

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
    private int maxSelectableChoices;
    private int minSelectableChoices;
    private boolean useChoiceLabels;
    private boolean saveCombinationPatterns;

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
        this.maxSelectableChoices = 1;
        this.minSelectableChoices = 1;
        this.useChoiceLabels = true;
        this.saveCombinationPatterns = true;
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

    public int getMaxSelectableChoices() {
        return maxSelectableChoices;
    }

    public void setMaxSelectableChoices(int maxSelectableChoices) {
        if (maxSelectableChoices < 1) {
            throw new IllegalArgumentException("最大選択可能数は1以上である必要があります");
        }
        this.maxSelectableChoices = maxSelectableChoices;
    }

    public int getMinSelectableChoices() {
        return minSelectableChoices;
    }

    public void setMinSelectableChoices(int minSelectableChoices) {
        if (minSelectableChoices < 1) {
            throw new IllegalArgumentException("必須選択数は1以上である必要があります");
        }
        this.minSelectableChoices = minSelectableChoices;
    }

    public boolean isUseChoiceLabels() {
        return useChoiceLabels;
    }

    public void setUseChoiceLabels(boolean useChoiceLabels) {
        this.useChoiceLabels = useChoiceLabels;
    }

    public boolean isSaveCombinationPatterns() {
        return saveCombinationPatterns;
    }

    public void setSaveCombinationPatterns(boolean saveCombinationPatterns) {
        this.saveCombinationPatterns = saveCombinationPatterns;
    }

    public String getButtonCreateQuestions() {
        return getOrDefault(buttonCreateQuestions, "問題を作成");
    }

    public void setButtonCreateQuestions(String buttonCreateQuestions) {
        this.buttonCreateQuestions = buttonCreateQuestions;
    }

    public String getButtonTakeSurvey() {
        return getOrDefault(buttonTakeSurvey, "アンケートに回答");
    }

    public void setButtonTakeSurvey(String buttonTakeSurvey) {
        this.buttonTakeSurvey = buttonTakeSurvey;
    }

    public String getButtonNextQuestion() {
        return getOrDefault(buttonNextQuestion, "次の問題へ");
    }

    public void setButtonNextQuestion(String buttonNextQuestion) {
        this.buttonNextQuestion = buttonNextQuestion;
    }

    public String getButtonPrevQuestion() {
        return getOrDefault(buttonPrevQuestion, "前の問題へ");
    }

    public void setButtonPrevQuestion(String buttonPrevQuestion) {
        this.buttonPrevQuestion = buttonPrevQuestion;
    }

    public String getButtonReselect() {
        return getOrDefault(buttonReselect, "選択肢を選び直す");
    }

    public void setButtonReselect(String buttonReselect) {
        this.buttonReselect = buttonReselect;
    }

    public String getButtonFinishSurvey() {
        return getOrDefault(buttonFinishSurvey, "回答を終了する");
    }

    public void setButtonFinishSurvey(String buttonFinishSurvey) {
        this.buttonFinishSurvey = buttonFinishSurvey;
    }

    public String getTitleMain() {
        return getOrDefault(titleMain, "研究用アンケートシステム");
    }

    public void setTitleMain(String titleMain) {
        this.titleMain = titleMain;
    }

    public String getTitleQuestionEditor() {
        return getOrDefault(titleQuestionEditor, "問題作成");
    }

    public void setTitleQuestionEditor(String titleQuestionEditor) {
        this.titleQuestionEditor = titleQuestionEditor;
    }

    public String getTitleSettings() {
        return getOrDefault(titleSettings, "設定");
    }

    public void setTitleSettings(String titleSettings) {
        this.titleSettings = titleSettings;
    }

    public String getTitleSurvey() {
        return getOrDefault(titleSurvey, "アンケート回答");
    }

    public void setTitleSurvey(String titleSurvey) {
        this.titleSurvey = titleSurvey;
    }

    public String getLogActionChoiceSelection() {
        return getOrDefault(logActionChoiceSelection, "選択肢選択");
    }

    public void setLogActionChoiceSelection(String logActionChoiceSelection) {
        this.logActionChoiceSelection = logActionChoiceSelection;
    }

    public String getLogActionReasonStart() {
        return getOrDefault(logActionReasonStart, "理由入力開始");
    }

    public void setLogActionReasonStart(String logActionReasonStart) {
        this.logActionReasonStart = logActionReasonStart;
    }

    public String getLogActionReasonText() {
        return getOrDefault(logActionReasonText, "理由入力内容");
    }

    public void setLogActionReasonText(String logActionReasonText) {
        this.logActionReasonText = logActionReasonText;
    }

    public String getLogActionReasonRewrite() {
        return getOrDefault(logActionReasonRewrite, "理由書き直し");
    }

    public void setLogActionReasonRewrite(String logActionReasonRewrite) {
        this.logActionReasonRewrite = logActionReasonRewrite;
    }

    public String getLogActionQuestionMove() {
        return getOrDefault(logActionQuestionMove, "問題移動");
    }

    public void setLogActionQuestionMove(String logActionQuestionMove) {
        this.logActionQuestionMove = logActionQuestionMove;
    }

    public String getLogActionSubmit() {
        return getOrDefault(logActionSubmit, "アンケート送信");
    }

    public void setLogActionSubmit(String logActionSubmit) {
        this.logActionSubmit = logActionSubmit;
    }

    private String getOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
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
        map.put("max_selectable_choices", maxSelectableChoices);
        map.put("min_selectable_choices", minSelectableChoices);
        map.put("use_choice_labels", useChoiceLabels);
        map.put("save_combination_patterns", saveCombinationPatterns);
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
        this.questionsDirectory = getString(map, "questions_directory");
        this.questionsFile = getString(map, "questions_file");
        this.logDirectory = getString(map, "log_directory");
        this.logNameFormat = getString(map, "log_name_format");
        this.responseDirectory = getString(map, "response_directory");
        this.responseNameFormat = getString(map, "response_name_format");
        this.participantName = getString(map, "participant_name");
        this.participantId = getString(map, "participant_id");
        this.appearanceMode = getString(map, "appearance_mode");
        this.colorTheme = getString(map, "color_theme");
        this.outputFormat = getString(map, "output_format");
        this.fontSize = getString(map, "font_size");
        this.autoSave = getBoolean(map, "auto_save", this.autoSave);
        this.useParticipantInfo = getBoolean(map, "use_participant_info", this.useParticipantInfo);
        this.defaultChoices = getInt(map, "default_choices", this.defaultChoices);
        this.choiceColumns = getInt(map, "choice_columns", this.choiceColumns);
        this.logSequence = getInt(map, "log_sequence", this.logSequence);
        this.responseSequence = getInt(map, "response_sequence", this.responseSequence);
        this.contentWidth = getInt(map, "content_width", this.contentWidth);
        this.useHtmlRendering = getBoolean(map, "use_html_rendering", this.useHtmlRendering);
        this.randomizeChoices = getBoolean(map, "randomize_choices", this.randomizeChoices);
        this.enablePrevButton = getBoolean(map, "enable_prev_button", this.enablePrevButton);
        this.maxSelectableChoices = getInt(map, "max_selectable_choices", this.maxSelectableChoices);
        this.minSelectableChoices = getInt(map, "min_selectable_choices", this.minSelectableChoices);
        this.useChoiceLabels = getBoolean(map, "use_choice_labels", this.useChoiceLabels);
        this.saveCombinationPatterns = getBoolean(map, "save_combination_patterns", this.saveCombinationPatterns);
        this.buttonCreateQuestions = getString(map, "button_create_questions");
        this.buttonTakeSurvey = getString(map, "button_take_survey");
        this.buttonNextQuestion = getString(map, "button_next_question");
        this.buttonPrevQuestion = getString(map, "button_prev_question");
        this.buttonReselect = getString(map, "button_reselect");
        this.buttonFinishSurvey = getString(map, "button_finish_survey");
        this.titleMain = getString(map, "title_main");
        this.titleQuestionEditor = getString(map, "title_question_editor");
        this.titleSettings = getString(map, "title_settings");
        this.titleSurvey = getString(map, "title_survey");
        this.logActionChoiceSelection = getString(map, "log_action_choice_selection");
        this.logActionReasonStart = getString(map, "log_action_reason_start");
        this.logActionReasonText = getString(map, "log_action_reason_text");
        this.logActionReasonRewrite = getString(map, "log_action_reason_rewrite");
        this.logActionQuestionMove = getString(map, "log_action_question_move");
        this.logActionSubmit = getString(map, "log_action_submit");
    }

    private String getString(Map<String, Object> map, String key) {
        return map.containsKey(key) ? (String) map.get(key) : null;
    }

    private boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        return map.containsKey(key) ? (Boolean) map.get(key) : defaultValue;
    }

    private int getInt(Map<String, Object> map, String key, int defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof Integer) return (Integer) value;
        return defaultValue;
    }
}
