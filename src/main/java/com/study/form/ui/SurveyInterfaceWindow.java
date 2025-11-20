package com.study.form.ui;

import com.study.form.Constants;
import com.study.form.model.Question;
import com.study.form.model.Response;
import com.study.form.util.ActionLogger;
import com.study.form.util.ConfigManager;
import com.study.form.util.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * アンケート回答インターフェースウィンドウ
 */
public class SurveyInterfaceWindow extends JFrame {
    private List<Question> questions = new ArrayList<>();
    private List<Response> responses = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private String respondentId;
    
    private ActionLogger logger;
    private ConfigManager configManager;
    
    // UI コンポーネント
    private JLabel progressLabel;
    private JEditorPane questionEditorPane;
    private JPanel choicesPanel;
    private JTextArea reasonTextArea;
    private JButton rewriteButton;
    private JLabel statusLabel;
    private JButton nextButton;
    private JButton prevButton;
    
    // 状態管理
    private String selectedChoice = null;
    private boolean reasonStarted = false;
    private List<JButton> choiceButtons = new ArrayList<>();
    
    public SurveyInterfaceWindow() {
        setTitle("アンケート回答");
        setSize(Constants.SURVEY_WINDOW_SIZE);
        setLocationRelativeTo(null);

        configManager = new ConfigManager();

        // 設定で被験者情報を使用する場合のみ入力画面を表示
        if (configManager.getConfig().isUseParticipantInfo()) {
            ParticipantInfoWindow infoWindow = new ParticipantInfoWindow(null);
            infoWindow.setVisible(true);

            // キャンセルされた場合は終了
            if (!infoWindow.isConfirmed()) {
                dispose();
                return;
            }
        }

        respondentId = UUID.randomUUID().toString().substring(0, 8);

        // ログファイルパスを取得
        String logPath = configManager.getLogPath(respondentId);
        logger = new ActionLogger(logPath);

        loadQuestionsDialog();
    }
    
    private void loadQuestionsDialog() {
        String filepath = configManager.getQuestionsPath();
        
        if (filepath == null || !new File(filepath).exists()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "CSV/JSONファイル (*.csv, *.json)", "csv", "json"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                filepath = fileChooser.getSelectedFile().getAbsolutePath();
            } else {
                dispose();
                return;
            }
        }
        
        questions = FileUtils.loadQuestions(filepath);
        
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "問題を読み込めませんでした", 
                "エラー", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        setupUI();
        displayQuestion();
    }
    
    /**
     * UIコンポーネントを初期化してレイアウトします。
     */
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        mainPanel.add(createNavigationPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * 進捗表示を含むヘッダーパネルを作成します。
     *
     * @return ヘッダーパネル
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        progressLabel = new JLabel();
        progressLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        headerPanel.add(progressLabel, BorderLayout.WEST);
        headerPanel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_LARGE, 0));
        return headerPanel;
    }

    /**
     * 質問、選択肢、理由入力を含むコンテンツパネルを作成します。
     *
     * @return スクロール可能なコンテンツパネル
     */
    private JScrollPane createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // 質問エディタペイン（HTML対応、縦スクロール対応）
        questionEditorPane = new JEditorPane();
        questionEditorPane.setContentType("text/html");
        questionEditorPane.setEditable(false);
        questionEditorPane.setFocusable(false);
        questionEditorPane.setOpaque(false);
        questionEditorPane.setBorder(new EmptyBorder(0, 0, Constants.PADDING_LARGE, 0));

        // フォントスタイルを設定
        String fontFamily = Constants.FONT_FAMILY;
        int fontSize = Constants.FONT_SIZE_SUBTITLE;
        questionEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        questionEditorPane.setFont(new Font(fontFamily, Font.BOLD, fontSize));

        JScrollPane questionScrollPane = new JScrollPane(questionEditorPane);
        questionScrollPane.setBorder(null);
        questionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        contentPanel.add(questionScrollPane);

        // 選択肢パネル
        choicesPanel = new JPanel();
        int columns = configManager.getConfig().getChoiceColumns();
        choicesPanel.setLayout(new GridLayout(0, columns, Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        choicesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(choicesPanel);

        contentPanel.add(Box.createVerticalStrut(Constants.VERTICAL_STRUT_LARGE));

        // 理由入力パネル
        contentPanel.add(createReasonPanel());

        // 状態ラベル
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        statusLabel.setForeground(Constants.COLOR_STATUS_ERROR);
        statusLabel.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, 0, 0));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusLabel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    /**
     * 理由入力エリアとボタンを含むパネルを作成します。
     *
     * @return 理由入力パネル
     */
    private JPanel createReasonPanel() {
        JPanel reasonPanel = new JPanel(new BorderLayout(Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        reasonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel reasonLabel = new JLabel("選択した理由を記入してください");
        reasonLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        reasonPanel.add(reasonLabel, BorderLayout.NORTH);

        reasonTextArea = new JTextArea(4, 40);
        reasonTextArea.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        reasonTextArea.setLineWrap(true);
        reasonTextArea.setWrapStyleWord(true);
        reasonTextArea.setEnabled(false);

        // 理由入力の監視
        reasonTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { onReasonKeyPress(); }
            public void removeUpdate(DocumentEvent e) { onReasonKeyPress(); }
            public void insertUpdate(DocumentEvent e) { onReasonKeyPress(); }
        });

        JScrollPane reasonScrollPane = new JScrollPane(reasonTextArea);
        reasonPanel.add(reasonScrollPane, BorderLayout.CENTER);

        // 書き直しボタン
        rewriteButton = new JButton("理由を書き直す");
        rewriteButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BUTTON));
        rewriteButton.setBackground(Constants.COLOR_GRAY);
        rewriteButton.setEnabled(false);
        rewriteButton.addActionListener(e -> rewriteReason());

        JPanel rewritePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rewritePanel.add(rewriteButton);
        reasonPanel.add(rewritePanel, BorderLayout.SOUTH);

        return reasonPanel;
    }

    /**
     * ナビゲーションボタンを含むパネルを作成します。
     *
     * @return ナビゲーションパネル
     */
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBorder(new EmptyBorder(Constants.PADDING_LARGE, 0, 0, 0));

        prevButton = new JButton("前の問題へ");
        prevButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_BUTTON));
        prevButton.setPreferredSize(new Dimension(150, 45));
        prevButton.setBackground(Constants.COLOR_GRAY);
        prevButton.setEnabled(false);
        prevButton.addActionListener(e -> prevQuestion());
        navPanel.add(prevButton, BorderLayout.WEST);

        nextButton = new JButton("次の問題へ");
        nextButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BUTTON));
        nextButton.setPreferredSize(new Dimension(150, 45));
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextQuestion());
        navPanel.add(nextButton, BorderLayout.EAST);

        return navPanel;
    }
    
    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            submitSurvey();
            return;
        }
        
        Question question = questions.get(currentQuestionIndex);
        
        // 進捗表示
        progressLabel.setText("問題 " + (currentQuestionIndex + 1) + " / " + questions.size());
        
        // 質問文表示（HTMLラップ）
        String questionText = question.getText();
        // HTMLタグが含まれていない場合は、自動的にラップ
        if (!questionText.trim().toLowerCase().startsWith("<html")) {
            questionText = "<html><body style='font-family: " + Constants.FONT_FAMILY +
                          "; font-size: " + Constants.FONT_SIZE_SUBTITLE + "pt; font-weight: bold;'>" +
                          questionText + "</body></html>";
        }
        questionEditorPane.setText(questionText);
        
        // 選択肢をクリア
        choicesPanel.removeAll();
        choiceButtons.clear();
        
        // 選択肢を表示
        for (int i = 0; i < question.getChoices().size(); i++) {
            String choice = question.getChoices().get(i);
            createChoiceButton(choice, i);
        }
        
        // 状態をリセット
        selectedChoice = null;
        reasonStarted = false;
        
        // 理由入力をクリアして無効化
        reasonTextArea.setEnabled(false);
        reasonTextArea.setText("");
        
        // ボタンの状態を更新
        rewriteButton.setEnabled(false);
        nextButton.setEnabled(false);
        statusLabel.setText(" ");
        
        // 前へボタンの状態
        prevButton.setEnabled(currentQuestionIndex > 0);
        
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }
    
    private void createChoiceButton(String choiceText, int index) {
        // HTMLタグで囲んで自動改行を有効化
        String htmlText = "<html><body style='width: 100%; text-align: left;'>" + choiceText + "</body></html>";
        JButton button = new JButton(htmlText);
        button.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_BUTTON));

        // サイズ設定（GridLayoutでは高さのみ指定）
        button.setPreferredSize(new Dimension(0, Constants.CHOICE_BUTTON_HEIGHT));

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);

        // デフォルトの色設定
        button.setBackground(Constants.COLOR_DEFAULT);
        button.setForeground(Constants.COLOR_DEFAULT_TEXT);

        // 余白のみ設定（ボーダーなし）
        button.setBorder(new EmptyBorder(12, 20, 12, 20));

        button.addActionListener(e -> selectChoice(choiceText, index));

        choiceButtons.add(button);
        choicesPanel.add(button);
    }
    
    private void selectChoice(String choiceText, int index) {
        // 理由を書き始めた後は選択不可
        if (reasonStarted) {
            statusLabel.setText(Constants.MSG_CHANGE_DISABLED_STATUS);
            JOptionPane.showMessageDialog(this, Constants.MSG_CANNOT_CHANGE_CHOICE,
                "変更できません", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 選択を更新
        selectedChoice = choiceText;
        
        // ログに記録
        logger.logChoiceSelection(currentQuestionIndex + 1, choiceText);
        
        // 選択したボタンの色を変更
        updateChoiceButtonColors(index);
        
        // 理由入力を有効化してリセット
        resetReasonInput();
    }
    
    private void updateChoiceButtonColors(int selectedIndex) {
        for (int i = 0; i < choiceButtons.size(); i++) {
            JButton button = choiceButtons.get(i);
            if (i == selectedIndex) {
                button.setBackground(Constants.COLOR_SELECTED);
                button.setForeground(Constants.COLOR_SELECTED_TEXT);
            } else {
                button.setBackground(Constants.COLOR_DEFAULT);
                button.setForeground(Constants.COLOR_DEFAULT_TEXT);
            }
            // ボーダーは変更しない（統一された余白を維持）
        }
    }
    
    private void resetReasonInput() {
        reasonTextArea.setEnabled(true);
        reasonTextArea.setText("");
        reasonStarted = false;
        rewriteButton.setEnabled(false);
        nextButton.setEnabled(false);
        statusLabel.setText(" ");
    }
    
    private void onReasonKeyPress() {
        if (!reasonStarted && reasonTextArea.getText().length() > 0) {
            reasonStarted = true;
            rewriteButton.setEnabled(true);
            nextButton.setEnabled(true);
            
            // ログに記録
            logger.logReasonStart(currentQuestionIndex + 1);
            
            statusLabel.setText(Constants.MSG_REASON_STARTED_STATUS);
            statusLabel.setForeground(Constants.COLOR_STATUS_WARNING);
        }
    }
    
    private void rewriteReason() {
        // ログに記録
        logger.logRewriteReason(currentQuestionIndex + 1);
        
        // 理由をクリア
        reasonTextArea.setText("");
        reasonStarted = false;
        
        // ボタンの状態を更新
        rewriteButton.setEnabled(false);
        nextButton.setEnabled(false);
        
        statusLabel.setText(Constants.MSG_CAN_CHANGE_STATUS);
        statusLabel.setForeground(Constants.COLOR_STATUS_SUCCESS);
    }
    
    private void nextQuestion() {
        if (selectedChoice == null) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_CHOICE_SELECTED,
                "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String reason = reasonTextArea.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_REASON,
                "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // ログに理由の内容を記録
        logger.logReasonText(currentQuestionIndex + 1, reason);
        
        // 回答を保存
        Response response = new Response(
            respondentId,
            FileUtils.getTimestamp(),
            currentQuestionIndex + 1,
            questions.get(currentQuestionIndex).getText(),
            selectedChoice,
            reason
        );
        
        responses.add(response);
        
        // ログに記録
        int oldIndex = currentQuestionIndex;
        currentQuestionIndex++;
        
        if (currentQuestionIndex < questions.size()) {
            logger.logNextQuestion(oldIndex + 1, currentQuestionIndex + 1);
        }
        
        // 次の問題を表示
        displayQuestion();
    }
    
    private void prevQuestion() {
        if (currentQuestionIndex > 0) {
            int oldIndex = currentQuestionIndex;
            currentQuestionIndex--;
            
            logger.logNextQuestion(oldIndex + 1, currentQuestionIndex + 1);
            
            // 前の回答があれば削除
            if (!responses.isEmpty()) {
                responses.remove(responses.size() - 1);
            }
            
            displayQuestion();
        }
    }
    
    private void submitSurvey() {
        logger.logSubmit();
        
        // 設定から回答ファイルのパスを取得
        String filepath = configManager.getResponsePath(respondentId);
        
        // ディレクトリが存在しない場合は作成
        if (filepath != null) {
            File responseDir = new File(filepath).getParentFile();
            if (responseDir != null && !responseDir.exists()) {
                responseDir.mkdirs();
            }
        }
        
        // 設定にパスがない場合は手動で保存先を選択
        if (filepath == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(Constants.RESPONSES_DIR));
            fileChooser.setSelectedFile(new File("responses_" + respondentId + ".csv"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                filepath = fileChooser.getSelectedFile().getAbsolutePath();
            }
        }
        
        if (filepath != null) {
            // 出力形式を取得
            String outputFormat = configManager.getConfig().getOutputFormat();
            
            // 拡張子を削除してベースパスを取得
            String baseFilepath = filepath.replace(".csv", "").replace(".json", "");
            
            // 保存
            if (FileUtils.saveResponse(responses, baseFilepath, outputFormat)) {
                StringBuilder message = new StringBuilder("アンケートが完了しました。\n回答を保存しました:\n");
                
                if ("csv".equals(outputFormat) || "both".equals(outputFormat)) {
                    message.append(baseFilepath).append(".csv\n");
                }
                if ("json".equals(outputFormat) || "both".equals(outputFormat)) {
                    message.append(baseFilepath).append(".json\n");
                }
                
                JOptionPane.showMessageDialog(this, message.toString(), "完了",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "保存に失敗しました", 
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        dispose();
    }
}
