package form.model;

import form.model.config.*;
import form.util.ConfigMapper;

import java.util.Map;

/**
 * アプリケーション設定データモデル
 *
 * 不変なRecordオブジェクトを使用した設定管理
 */
public class Config {
    // 8つのRecordフィールド
    private final FileSettings fileSettings;
    private final ParticipantSettings participantSettings;
    private final UISettings uiSettings;
    private final BehaviorSettings behaviorSettings;
    private final RecordingSettings recordingSettings;
    private final ButtonLabels buttonLabels;
    private final WindowTitles windowTitles;
    private final LogActionNames logActionNames;

    /**
     * 全てのRecordを指定してConfigを作成
     */
    public Config(
            FileSettings fileSettings,
            ParticipantSettings participantSettings,
            UISettings uiSettings,
            BehaviorSettings behaviorSettings,
            RecordingSettings recordingSettings,
            ButtonLabels buttonLabels,
            WindowTitles windowTitles,
            LogActionNames logActionNames
    ) {
        this.fileSettings = fileSettings != null ? fileSettings : FileSettings.createDefault();
        this.participantSettings = participantSettings != null ? participantSettings : ParticipantSettings.createDefault();
        this.uiSettings = uiSettings != null ? uiSettings : UISettings.createDefault();
        this.behaviorSettings = behaviorSettings != null ? behaviorSettings : BehaviorSettings.createDefault();
        this.recordingSettings = recordingSettings != null ? recordingSettings : RecordingSettings.createDefault();
        this.buttonLabels = buttonLabels != null ? buttonLabels : ButtonLabels.createDefault();
        this.windowTitles = windowTitles != null ? windowTitles : WindowTitles.createDefault();
        this.logActionNames = logActionNames != null ? logActionNames : LogActionNames.createDefault();
    }

    /**
     * デフォルト値でConfigを作成
     */
    public Config() {
        this(null, null, null, null, null, null, null, null);
    }

    // ========== Record アクセサ ==========

    public FileSettings fileSettings() {
        return fileSettings;
    }

    public ParticipantSettings participantSettings() {
        return participantSettings;
    }

    public UISettings uiSettings() {
        return uiSettings;
    }

    public BehaviorSettings behaviorSettings() {
        return behaviorSettings;
    }

    public RecordingSettings recordingSettings() {
        return recordingSettings;
    }

    public ButtonLabels buttonLabels() {
        return buttonLabels;
    }

    public WindowTitles windowTitles() {
        return windowTitles;
    }

    public LogActionNames logActionNames() {
        return logActionNames;
    }

    // ========== 下位互換性のための委譲ゲッター ==========
    // 既存のコードが段階的に移行できるように残す

    // FileSettings関連
    public String getQuestionsDirectory() {
        return fileSettings.questionsDirectory();
    }

    public String getQuestionsFile() {
        return fileSettings.questionsFile();
    }

    public String getLogDirectory() {
        return fileSettings.logDirectory();
    }

    public String getLogNameFormat() {
        return fileSettings.logNameFormat();
    }

    public String getResponseDirectory() {
        return fileSettings.responseDirectory();
    }

    public String getResponseNameFormat() {
        return fileSettings.responseNameFormat();
    }

    public int getLogSequence() {
        return fileSettings.logSequence();
    }

    public int getResponseSequence() {
        return fileSettings.responseSequence();
    }

    // ParticipantSettings関連
    public String getParticipantName() {
        return participantSettings.participantName();
    }

    public String getParticipantId() {
        return participantSettings.participantId();
    }

    public boolean isUseParticipantInfo() {
        return participantSettings.useParticipantInfo();
    }

    // UISettings関連
    public String getAppearanceMode() {
        return uiSettings.appearanceMode();
    }

    public String getColorTheme() {
        return uiSettings.colorTheme();
    }

    public String getOutputFormat() {
        return uiSettings.outputFormat();
    }

    public String getFontSize() {
        return uiSettings.fontSize();
    }

    public boolean isUseHtmlRendering() {
        return uiSettings.useHtmlRendering();
    }

    public int getContentWidth() {
        return uiSettings.contentWidth();
    }

    // BehaviorSettings関連
    public boolean isAutoSave() {
        return behaviorSettings.autoSave();
    }

    public int getDefaultChoices() {
        return behaviorSettings.defaultChoices();
    }

    public int getChoiceColumns() {
        return behaviorSettings.choiceColumns();
    }

    public boolean isRandomizeChoices() {
        return behaviorSettings.randomizeChoices();
    }

    public boolean isEnablePrevButton() {
        return behaviorSettings.enablePrevButton();
    }

    public int getMaxSelectableChoices() {
        return behaviorSettings.maxSelectableChoices();
    }

    public int getMinSelectableChoices() {
        return behaviorSettings.minSelectableChoices();
    }

    public boolean isUseChoiceLabels() {
        return behaviorSettings.useChoiceLabels();
    }

    public boolean isSaveCombinationPatterns() {
        return behaviorSettings.saveCombinationPatterns();
    }

    // RecordingSettings関連
    public boolean isEnableReasonRecording() {
        return recordingSettings.enableReasonRecording();
    }

    public boolean isEnableThinkAloud() {
        return recordingSettings.enableThinkAloud();
    }

    public String getAudioDirectory() {
        return recordingSettings.audioDirectory();
    }

    // ButtonLabels関連
    public String getButtonCreateQuestions() {
        return buttonLabels.buttonCreateQuestions();
    }

    public String getButtonTakeSurvey() {
        return buttonLabels.buttonTakeSurvey();
    }

    public String getButtonNextQuestion() {
        return buttonLabels.buttonNextQuestion();
    }

    public String getButtonPrevQuestion() {
        return buttonLabels.buttonPrevQuestion();
    }

    public String getButtonReselect() {
        return buttonLabels.buttonReselect();
    }

    public String getButtonFinishSurvey() {
        return buttonLabels.buttonFinishSurvey();
    }

    // WindowTitles関連
    public String getTitleMain() {
        return windowTitles.titleMain();
    }

    public String getTitleQuestionEditor() {
        return windowTitles.titleQuestionEditor();
    }

    public String getTitleSettings() {
        return windowTitles.titleSettings();
    }

    public String getTitleSurvey() {
        return windowTitles.titleSurvey();
    }

    // LogActionNames関連
    public String getLogActionChoiceSelection() {
        return logActionNames.logActionChoiceSelection();
    }

    public String getLogActionReasonStart() {
        return logActionNames.logActionReasonStart();
    }

    public String getLogActionReasonText() {
        return logActionNames.logActionReasonText();
    }

    public String getLogActionReasonRewrite() {
        return logActionNames.logActionReasonRewrite();
    }

    public String getLogActionQuestionMove() {
        return logActionNames.logActionQuestionMove();
    }

    public String getLogActionSubmit() {
        return logActionNames.logActionSubmit();
    }

    // ========== Map変換 (ConfigMapperへ委譲) ==========

    /**
     * ConfigオブジェクトをMapに変換
     * @return 設定データを含むマップ
     */
    public Map<String, Object> toMap() {
        return ConfigMapper.toMap(this);
    }

    /**
     * MapからConfigオブジェクトを作成（静的ファクトリーメソッド）
     * @param map 設定データを含むマップ
     * @return 作成されたConfigオブジェクト
     */
    public static Config fromMap(Map<String, Object> map) {
        return ConfigMapper.fromMap(map);
    }

    // ========== セッター（非推奨 - 新しいインスタンスを作成） ==========
    // Recordは不変なので、セッターは使用できません
    // 設定を更新する場合は、新しいRecordインスタンスを作成してConfigを再構築してください

    /**
     * @deprecated Configは不変です。新しいFileSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setQuestionsDirectory(String questionsDirectory) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated FileSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいFileSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setQuestionsFile(String questionsFile) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated FileSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいFileSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogDirectory(String logDirectory) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated FileSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいFileSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogNameFormat(String logNameFormat) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated FileSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいFileSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setResponseDirectory(String responseDirectory) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated FileSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいFileSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setResponseNameFormat(String responseNameFormat) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated FileSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいFileSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogSequence(int logSequence) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated FileSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいFileSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setResponseSequence(int responseSequence) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated FileSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいParticipantSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setParticipantName(String participantName) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ParticipantSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいParticipantSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setParticipantId(String participantId) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ParticipantSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいParticipantSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setUseParticipantInfo(boolean useParticipantInfo) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ParticipantSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいUISettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setAppearanceMode(String appearanceMode) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated UISettings.");
    }

    /**
     * @deprecated Configは不変です。新しいUISettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setColorTheme(String colorTheme) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated UISettings.");
    }

    /**
     * @deprecated Configは不変です。新しいUISettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setOutputFormat(String outputFormat) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated UISettings.");
    }

    /**
     * @deprecated Configは不変です。新しいUISettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setFontSize(String fontSize) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated UISettings.");
    }

    /**
     * @deprecated Configは不変です。新しいUISettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setUseHtmlRendering(boolean useHtmlRendering) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated UISettings.");
    }

    /**
     * @deprecated Configは不変です。新しいUISettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setContentWidth(int contentWidth) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated UISettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setAutoSave(boolean autoSave) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setDefaultChoices(int defaultChoices) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setChoiceColumns(int choiceColumns) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setRandomizeChoices(boolean randomizeChoices) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setEnablePrevButton(boolean enablePrevButton) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setMaxSelectableChoices(int maxSelectableChoices) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setMinSelectableChoices(int minSelectableChoices) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setUseChoiceLabels(boolean useChoiceLabels) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいBehaviorSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setSaveCombinationPatterns(boolean saveCombinationPatterns) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated BehaviorSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいRecordingSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setEnableReasonRecording(boolean enableReasonRecording) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated RecordingSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいRecordingSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setEnableThinkAloud(boolean enableThinkAloud) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated RecordingSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいRecordingSettingsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setAudioDirectory(String audioDirectory) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated RecordingSettings.");
    }

    /**
     * @deprecated Configは不変です。新しいButtonLabelsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setButtonCreateQuestions(String buttonCreateQuestions) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ButtonLabels.");
    }

    /**
     * @deprecated Configは不変です。新しいButtonLabelsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setButtonTakeSurvey(String buttonTakeSurvey) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ButtonLabels.");
    }

    /**
     * @deprecated Configは不変です。新しいButtonLabelsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setButtonNextQuestion(String buttonNextQuestion) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ButtonLabels.");
    }

    /**
     * @deprecated Configは不変です。新しいButtonLabelsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setButtonPrevQuestion(String buttonPrevQuestion) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ButtonLabels.");
    }

    /**
     * @deprecated Configは不変です。新しいButtonLabelsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setButtonReselect(String buttonReselect) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ButtonLabels.");
    }

    /**
     * @deprecated Configは不変です。新しいButtonLabelsインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setButtonFinishSurvey(String buttonFinishSurvey) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated ButtonLabels.");
    }

    /**
     * @deprecated Configは不変です。新しいWindowTitlesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setTitleMain(String titleMain) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated WindowTitles.");
    }

    /**
     * @deprecated Configは不変です。新しいWindowTitlesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setTitleQuestionEditor(String titleQuestionEditor) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated WindowTitles.");
    }

    /**
     * @deprecated Configは不変です。新しいWindowTitlesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setTitleSettings(String titleSettings) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated WindowTitles.");
    }

    /**
     * @deprecated Configは不変です。新しいWindowTitlesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setTitleSurvey(String titleSurvey) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated WindowTitles.");
    }

    /**
     * @deprecated Configは不変です。新しいLogActionNamesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogActionChoiceSelection(String logActionChoiceSelection) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated LogActionNames.");
    }

    /**
     * @deprecated Configは不変です。新しいLogActionNamesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogActionReasonStart(String logActionReasonStart) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated LogActionNames.");
    }

    /**
     * @deprecated Configは不変です。新しいLogActionNamesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogActionReasonText(String logActionReasonText) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated LogActionNames.");
    }

    /**
     * @deprecated Configは不変です。新しいLogActionNamesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogActionReasonRewrite(String logActionReasonRewrite) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated LogActionNames.");
    }

    /**
     * @deprecated Configは不変です。新しいLogActionNamesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogActionQuestionMove(String logActionQuestionMove) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated LogActionNames.");
    }

    /**
     * @deprecated Configは不変です。新しいLogActionNamesインスタンスを作成して新しいConfigを構築してください
     */
    @Deprecated
    public void setLogActionSubmit(String logActionSubmit) {
        throw new UnsupportedOperationException("Config is now immutable. Create a new Config instance with updated LogActionNames.");
    }
}
