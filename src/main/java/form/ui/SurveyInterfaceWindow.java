package form.ui;

import form.Constants;
import form.model.Question;
import form.ui.manager.ChoiceManager;
import form.ui.manager.ReasonManager;
import form.ui.manager.RecordingManager;
import form.ui.manager.SurveyManager;
import form.ui.survey.*;
import form.util.ActionLogger;
import form.util.ConfigManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.UUID;

/**
 * アンケート回答インターフェースウィンドウ（リファクタリング版）
 * SurveyManagerファサードを使用して簡素化されたアンケート機能を提供します
 */
public class SurveyInterfaceWindow extends JFrame implements
        ChoiceManager.ChoiceSelectionListener,
        ReasonManager.ReasonInputListener {

    private final SurveyState state;
    private final ConfigManager configManager;
    private final SurveyManager surveyManager;

    public SurveyInterfaceWindow() {
        // 初期化
        configManager = new ConfigManager();
        state = new SurveyState();

        setTitle(configManager.getConfig().getTitleSurvey());
        setSize(Constants.SURVEY_WINDOW_SIZE);
        setLocationRelativeTo(null);

        // 被験者情報の入力
        if (configManager.getConfig().isUseParticipantInfo()) {
            var infoWindow = new ParticipantInfoWindow(null);
            infoWindow.setVisible(true);

            if (!infoWindow.isConfirmed()) {
                // 早期リターン時のダミー初期化（実際には使用されない）
                surveyManager = null;
                dispose();
                return;
            }
        }

        // 被験者ID生成
        var respondentId = UUID.randomUUID().toString().substring(0, 8);
        state.setRespondentId(respondentId);

        // ログ初期化
        var logPath = configManager.getLogPath(respondentId);
        var logger = new ActionLogger(logPath, configManager.getConfig());

        // SurveyManager初期化
        surveyManager = new SurveyManager(configManager, logger, state, respondentId, this);

        // ウィンドウを閉じるときにクリーンアップ
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (surveyManager != null) {
                    surveyManager.cleanup();
                }
            }
        });

        // 問題を読み込む
        if (!surveyManager.loadQuestions()) {
            dispose();
            return;
        }

        // UI構築
        add(surveyManager.buildUI());

        // マネージャー初期化
        surveyManager.initialize(this, this);

        // イベントハンドラー設定
        setupEventHandlers();

        // 最初の問題を表示
        displayQuestion();
    }

    /**
     * イベントハンドラーを設定します
     */
    private void setupEventHandlers() {
        // 理由入力テキストエリアにリスナーを追加
        surveyManager.getReasonTextArea().getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { surveyManager.getReasonManager().onReasonKeyPress(); }
            public void removeUpdate(DocumentEvent e) { surveyManager.getReasonManager().onReasonKeyPress(); }
            public void insertUpdate(DocumentEvent e) { surveyManager.getReasonManager().onReasonKeyPress(); }
        });

        // 理由書き直しボタンのイベントハンドラー
        surveyManager.getRewriteButton().addActionListener(e ->
            surveyManager.getReasonManager().rewriteReason());

        // 理由録音ボタンのイベントハンドラー（ボタンが存在する場合のみ）
        if (surveyManager.getReasonRecordButton() != null) {
            surveyManager.getReasonRecordButton().addActionListener(e ->
                handleReasonRecordingToggle());
        }

        // Think-aloud録音ボタンのイベントハンドラー（ボタンが存在する場合のみ）
        if (surveyManager.getThinkAloudRecordButton() != null) {
            surveyManager.getThinkAloudRecordButton().addActionListener(e ->
                handleThinkAloudRecordingToggle());
        }

        // ナビゲーションボタンのイベントハンドラー
        surveyManager.getNextButton().addActionListener(e -> handleNextQuestion());
        surveyManager.getPrevButton().addActionListener(e -> handlePrevQuestion());
    }

    /**
     * 現在の問題を表示します
     */
    private void displayQuestion() {
        if (surveyManager.isCompleted()) {
            submitSurvey();
            return;
        }

        var question = state.getCurrentQuestion();
        var index = state.getCurrentQuestionIndex();

        // 問題を表示
        surveyManager.displayQuestion(question, index);

        // 進捗とボタンを更新
        surveyManager.getProgressLabel().setText(
            "問題 " + (index + 1) + " / " + state.getTotalQuestions());
        updateNavigationButtons();

        surveyManager.getChoicesPanel().revalidate();
        surveyManager.getChoicesPanel().repaint();
    }

    /**
     * ナビゲーションボタンの状態を更新します
     */
    private void updateNavigationButtons() {
        surveyManager.getPrevButton().setEnabled(
            configManager.getConfig().isEnablePrevButton() &&
            !state.isFirstQuestion());

        if (state.isLastQuestion()) {
            surveyManager.getNextButton().setText(
                configManager.getConfig().getButtonFinishSurvey());
        } else {
            surveyManager.getNextButton().setText(
                configManager.getConfig().getButtonNextQuestion());
        }
    }

    /**
     * 次の問題へ進む処理
     */
    private void handleNextQuestion() {
        if (surveyManager.moveToNext()) {
            displayQuestion();
        }
    }

    /**
     * 前の問題へ戻る処理
     */
    private void handlePrevQuestion() {
        if (surveyManager.moveToPrevious()) {
            displayQuestion();
        }
    }

    /**
     * アンケートを送信します
     */
    private void submitSurvey() {
        surveyManager.submitSurvey();
        dispose();
    }

    /**
     * 理由録音の切り替えを処理します
     */
    private void handleReasonRecordingToggle() {
        try {
            var recordingManager = surveyManager.getRecordingManager();
            var wasRecording = recordingManager.isReasonRecording();
            recordingManager.toggleReasonRecording();

            // 録音停止時に次へボタンを有効化し、自動的に次へ進む
            if (wasRecording && !recordingManager.isReasonRecording()) {
                surveyManager.getReasonManager().onRecordingCompleted();

                // 少し遅延してから自動的に次の問題へ進む
                var timer = new Timer(1000, e -> {
                    if (surveyManager.moveToNext()) {
                        displayQuestion();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        } catch (RecordingManager.RecordingException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "録音エラー",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Think-aloud録音の切り替えを処理します
     */
    private void handleThinkAloudRecordingToggle() {
        try {
            surveyManager.getRecordingManager().toggleThinkAloudRecording();
        } catch (RecordingManager.RecordingException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "録音エラー",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ChoiceManager.ChoiceSelectionListener の実装

    @Override
    public void onSelectionChanged(int selectedCount) {
        var reasonManager = surveyManager.getReasonManager();
        var choiceManager = surveyManager.getChoiceManager();
        reasonManager.updateReasonInputState(selectedCount);
        choiceManager.setReasonStarted(reasonManager.isReasonStarted());
    }

    @Override
    public void onCannotChangeChoice() {
        surveyManager.getStatusLabel().setText(Constants.MSG_CHANGE_DISABLED_STATUS);
        JOptionPane.showMessageDialog(this, Constants.MSG_CANNOT_CHANGE_CHOICE,
            "変更できません", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void onMaxChoicesExceeded(int maxCount) {
        JOptionPane.showMessageDialog(this,
            "選択できる選択肢は最大" + maxCount + "個までです",
            "選択数超過", JOptionPane.WARNING_MESSAGE);
    }

    // ReasonManager.ReasonInputListener の実装

    @Override
    public void onReasonInputEnabled() {
        surveyManager.getRecordingManager().enableReasonRecordButton();
    }

    @Override
    public void onReasonInputDisabled() {
        surveyManager.getRecordingManager().disableReasonRecordButton();
    }

    @Override
    public void onReasonInputStarted() {
        surveyManager.getChoiceManager().setReasonStarted(true);
    }

    @Override
    public void onReasonRewritten() {
        surveyManager.getChoiceManager().setReasonStarted(false);
    }
}
