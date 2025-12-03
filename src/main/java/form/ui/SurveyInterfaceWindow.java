package form.ui;

import form.Constants;
import form.model.Question;
import form.model.Response;
import form.util.ActionLogger;
import form.util.ConfigManager;
import form.util.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
    
    private JLabel progressLabel;
    private JEditorPane questionEditorPane;
    private JTextArea questionTextArea;
    private JScrollPane questionScrollPane;
    private JPanel choicesPanel;
    private JTextArea reasonTextArea;
    private JButton rewriteButton;
    private JLabel statusLabel;
    private JButton nextButton;
    private JButton prevButton;

    private List<String> selectedChoices = new ArrayList<>();
    private List<String> choiceTexts = new ArrayList<>();
    private boolean reasonStarted = false;
    private List<JButton> choiceButtons = new ArrayList<>();
    private Map<String, Integer> choiceIndexMap = new HashMap<>();
    
    public SurveyInterfaceWindow() {
        configManager = new ConfigManager();
        setTitle(configManager.getConfig().getTitleSurvey());
        setSize(Constants.SURVEY_WINDOW_SIZE);
        setLocationRelativeTo(null);

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
        logger = new ActionLogger(logPath, configManager.getConfig());

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
    
    private void setupUI() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        int fixedWidth = configManager.getConfig().getContentWidth();
        mainPanel.setPreferredSize(new Dimension(fixedWidth, 0));
        mainPanel.setMinimumSize(new Dimension(fixedWidth, 0));
        mainPanel.setMaximumSize(new Dimension(fixedWidth, Integer.MAX_VALUE));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        mainPanel.add(createNavigationPanel(), BorderLayout.SOUTH);

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.X_AXIS));
        centerWrapper.add(Box.createHorizontalGlue());
        centerWrapper.add(mainPanel);
        centerWrapper.add(Box.createHorizontalGlue());

        outerPanel.add(centerWrapper, BorderLayout.CENTER);
        add(outerPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        progressLabel = new JLabel();
        progressLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        headerPanel.add(progressLabel, BorderLayout.WEST);
        headerPanel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_LARGE, 0));
        return headerPanel;
    }

    private JScrollPane createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        boolean useHtml = configManager.getConfig().isUseHtmlRendering();

        if (useHtml) {
            questionEditorPane = new JEditorPane();
            questionEditorPane.setContentType("text/html");
            questionEditorPane.setEditable(false);
            questionEditorPane.setFocusable(false);
            questionEditorPane.setOpaque(false);
            questionEditorPane.setBorder(new EmptyBorder(0, 0, Constants.PADDING_LARGE, 0));

            String fontFamily = Constants.FONT_FAMILY;
            int fontSize = Constants.FONT_SIZE_SUBTITLE;
            questionEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            questionEditorPane.setFont(new Font(fontFamily, Font.BOLD, fontSize));

            questionScrollPane = new JScrollPane(questionEditorPane);
            questionScrollPane.setBorder(null);
            questionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            questionScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.QUESTION_SCROLL_HEIGHT_HTML));
            contentPanel.add(questionScrollPane);
        } else {
            questionTextArea = new JTextArea();
            questionTextArea.setEditable(false);
            questionTextArea.setFocusable(false);
            questionTextArea.setLineWrap(true);
            questionTextArea.setWrapStyleWord(true);
            questionTextArea.setOpaque(false);
            questionTextArea.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_SUBTITLE));
            questionTextArea.setBorder(new EmptyBorder(0, 0, Constants.PADDING_LARGE, 0));

            questionScrollPane = new JScrollPane(questionTextArea);
            questionScrollPane.setBorder(null);
            questionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            questionScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.QUESTION_SCROLL_HEIGHT_TEXT));
            contentPanel.add(questionScrollPane);
        }

        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        choicesPanel.setBorder(BorderFactory.createEmptyBorder(Constants.PADDING_MEDIUM, 0, Constants.PADDING_MEDIUM, 0));
        contentPanel.add(choicesPanel);

        contentPanel.add(Box.createVerticalStrut(Constants.PADDING_EXTRA_LARGE));
        contentPanel.add(createReasonPanel());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        statusLabel.setForeground(Constants.COLOR_STATUS_ERROR);
        statusLabel.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, 0, 0));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusLabel);

        // 理由欄の下に空要素を追加して、余白を埋める
        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createReasonPanel() {
        JPanel reasonPanel = new JPanel(new BorderLayout(Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        reasonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reasonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.REASON_PANEL_HEIGHT));

        JLabel reasonLabel = new JLabel("選択した理由を記入してください");
        reasonLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        reasonPanel.add(reasonLabel, BorderLayout.NORTH);

        reasonTextArea = new JTextArea(3, 40);
        reasonTextArea.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        reasonTextArea.setLineWrap(true);
        reasonTextArea.setWrapStyleWord(true);
        reasonTextArea.setEnabled(false);
        reasonTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { onReasonKeyPress(); }
            public void removeUpdate(DocumentEvent e) { onReasonKeyPress(); }
            public void insertUpdate(DocumentEvent e) { onReasonKeyPress(); }
        });

        JScrollPane reasonScrollPane = new JScrollPane(reasonTextArea);
        reasonScrollPane.setPreferredSize(new Dimension(0, Constants.REASON_TEXT_AREA_HEIGHT));
        reasonScrollPane.setMinimumSize(new Dimension(0, Constants.REASON_TEXT_AREA_HEIGHT));
        reasonScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.REASON_TEXT_AREA_HEIGHT));
        reasonPanel.add(reasonScrollPane, BorderLayout.CENTER);

        rewriteButton = new JButton(configManager.getConfig().getButtonReselect());
        rewriteButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BUTTON));
        rewriteButton.setBackground(Constants.COLOR_GRAY);
        rewriteButton.setEnabled(false);
        rewriteButton.addActionListener(e -> rewriteReason());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(rewriteButton);
        reasonPanel.add(buttonPanel, BorderLayout.SOUTH);

        return reasonPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBorder(new EmptyBorder(Constants.PADDING_LARGE, 0, 0, 0));

        prevButton = new JButton(configManager.getConfig().getButtonPrevQuestion());
        prevButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_BUTTON));
        prevButton.setPreferredSize(new Dimension(Constants.NAVIGATION_BUTTON_WIDTH, Constants.NAVIGATION_BUTTON_HEIGHT));
        prevButton.setBackground(Constants.COLOR_GRAY);
        prevButton.setEnabled(false);
        prevButton.addActionListener(e -> prevQuestion());
        navPanel.add(prevButton, BorderLayout.WEST);

        nextButton = new JButton(configManager.getConfig().getButtonNextQuestion());
        nextButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BUTTON));
        nextButton.setPreferredSize(new Dimension(Constants.NAVIGATION_BUTTON_WIDTH, Constants.NAVIGATION_BUTTON_HEIGHT));
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

        progressLabel.setText("問題 " + (currentQuestionIndex + 1) + " / " + questions.size());
        displayQuestionText(question.getText());
        displayChoices(question.getChoices());
        resetQuestionState();
        updateNavigationButtons();

        choicesPanel.revalidate();
        choicesPanel.repaint();
    }

    private void displayQuestionText(String questionText) {
        boolean useHtml = configManager.getConfig().isUseHtmlRendering();

        if (useHtml) {
            questionEditorPane.setText(formatHtmlQuestion(questionText));
        } else {
            questionTextArea.setText(questionText);
        }
    }

    private String formatHtmlQuestion(String questionText) {
        String cssStyle = "body { font-family: '" + Constants.FONT_FAMILY +
                         "'; font-size: " + Constants.FONT_SIZE_SUBTITLE + "pt; font-weight: normal; }";

        if (!questionText.trim().toLowerCase().startsWith("<html")) {
            return "<html><head><style>" + cssStyle + "</style></head><body>" +
                  questionText + "</body></html>";
        }

        if (!questionText.toLowerCase().contains("<style>")) {
            questionText = questionText.replaceFirst("(?i)<head>",
                "<head><style>" + cssStyle + "</style>");
            if (!questionText.toLowerCase().contains("<head>")) {
                questionText = questionText.replaceFirst("(?i)<html>",
                    "<html><head><style>" + cssStyle + "</style></head>");
            }
        }
        return questionText;
    }

    private void displayChoices(List<String> originalChoices) {
        choicesPanel.removeAll();
        choiceButtons.clear();
        choiceTexts.clear();
        choiceIndexMap.clear();

        List<String> choices = new ArrayList<>(originalChoices);
        if (configManager.getConfig().isRandomizeChoices()) {
            Collections.shuffle(choices);
        }

        // 各選択肢のインデックスをマッピング
        for (int i = 0; i < choices.size(); i++) {
            choiceIndexMap.put(choices.get(i), i);
        }

        int columns = configManager.getConfig().getChoiceColumns();
        JPanel currentRow = null;

        for (int i = 0; i < choices.size(); i++) {
            if (i % columns == 0) {
                if (i > 0) {
                    choicesPanel.add(Box.createVerticalStrut(Constants.PADDING_MEDIUM));
                }
                currentRow = createChoiceRow(columns);
                choicesPanel.add(currentRow);
            }

            String choice = choices.get(i);
            choiceTexts.add(choice);
            createChoiceButton(choice, i, currentRow);
        }

        fillEmptyChoiceSlots(currentRow, columns, choices.size());
    }

    private JPanel createChoiceRow(int columns) {
        JPanel row = new JPanel();
        if (columns == 1) {
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setBorder(BorderFactory.createEmptyBorder(0, Constants.PADDING_MEDIUM, 0, Constants.PADDING_MEDIUM));
        } else {
            row.setLayout(new GridLayout(1, columns, Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        }
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return row;
    }

    private void fillEmptyChoiceSlots(JPanel currentRow, int columns, int choiceCount) {
        if (currentRow != null && columns > 1 && choiceCount % columns != 0) {
            int remaining = columns - (choiceCount % columns);
            for (int i = 0; i < remaining; i++) {
                currentRow.add(new JPanel());
            }
        }
    }

    private void resetQuestionState() {
        selectedChoices.clear();
        reasonStarted = false;
        reasonTextArea.setEnabled(false);
        reasonTextArea.setText("");
        rewriteButton.setEnabled(false);
        nextButton.setEnabled(false);
        statusLabel.setText(" ");
    }

    private void updateNavigationButtons() {
        prevButton.setEnabled(configManager.getConfig().isEnablePrevButton() && currentQuestionIndex > 0);

        if (currentQuestionIndex == questions.size() - 1) {
            nextButton.setText(configManager.getConfig().getButtonFinishSurvey());
        } else {
            nextButton.setText(configManager.getConfig().getButtonNextQuestion());
        }
    }
    
    private void createChoiceButton(String choiceText, int index, JPanel parentRow) {
        JTextArea textArea = createChoiceTextArea(choiceText);
        JPanel panel = createChoicePanel(textArea);

        java.awt.event.MouseAdapter clickAdapter = createChoiceMouseAdapter(choiceText, index, panel, textArea);
        panel.addMouseListener(clickAdapter);
        textArea.addMouseListener(clickAdapter);

        JButton dummyButton = createDummyButton(panel, textArea);
        choiceButtons.add(dummyButton);
        parentRow.add(panel);
    }

    /**
     * 選択肢用のテキストエリアを作成します。
     */
    private JTextArea createChoiceTextArea(String choiceText) {
        JTextArea textArea = new JTextArea(choiceText);
        textArea.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_BUTTON));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBorder(new EmptyBorder(10, 16, 10, 16));
        textArea.setBackground(Constants.COLOR_DEFAULT);
        textArea.setForeground(Constants.COLOR_DEFAULT_TEXT);
        textArea.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return textArea;
    }

    /**
     * 選択肢用のパネルを作成します。
     */
    private JPanel createChoicePanel(JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.COLOR_DEFAULT);
        panel.add(textArea, BorderLayout.CENTER);
        panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return panel;
    }

    /**
     * 選択肢用のマウスアダプターを作成します。
     */
    private java.awt.event.MouseAdapter createChoiceMouseAdapter(String choiceText, int index,
                                                                   JPanel panel, JTextArea textArea) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectChoice(choiceText, index);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!selectedChoices.contains(choiceText)) {
                    updateChoiceColors(panel, textArea, Constants.COLOR_DEFAULT.darker());
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                Color backgroundColor = selectedChoices.contains(choiceText)
                    ? Constants.COLOR_SELECTED
                    : Constants.COLOR_DEFAULT;
                updateChoiceColors(panel, textArea, backgroundColor);
            }
        };
    }

    /**
     * 選択肢の背景色を更新します。
     */
    private void updateChoiceColors(JPanel panel, JTextArea textArea, Color backgroundColor) {
        panel.setBackground(backgroundColor);
        textArea.setBackground(backgroundColor);
    }

    /**
     * ダミーボタンを作成します（既存の選択ロジック用）。
     */
    private JButton createDummyButton(JPanel panel, JTextArea textArea) {
        JButton dummyButton = new JButton();
        dummyButton.setVisible(false);
        dummyButton.putClientProperty("panel", panel);
        dummyButton.putClientProperty("textArea", textArea);
        return dummyButton;
    }
    
    private void selectChoice(String choiceText, int index) {
        if (isReasonStarted()) {
            showCannotChangeChoiceDialog();
            return;
        }

        if (selectedChoices.contains(choiceText)) {
            deselectChoice(choiceText);
        } else {
            if (!canAddMoreChoices()) {
                showMaxChoicesExceededDialog();
                return;
            }
            addChoice(choiceText);
        }

        updateChoiceButtonColors();
        updateReasonInputState();
    }

    /**
     * 理由の入力が開始されているかチェックします。
     */
    private boolean isReasonStarted() {
        if (reasonStarted) {
            statusLabel.setText(Constants.MSG_CHANGE_DISABLED_STATUS);
            return true;
        }
        return false;
    }

    /**
     * 選択変更不可のダイアログを表示します。
     */
    private void showCannotChangeChoiceDialog() {
        JOptionPane.showMessageDialog(this, Constants.MSG_CANNOT_CHANGE_CHOICE,
            "変更できません", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * 選択肢を選択解除します。
     */
    private void deselectChoice(String choiceText) {
        selectedChoices.remove(choiceText);
        String logText = getChoiceLogText(choiceText, "選択解除: ");
        logger.logChoiceSelection(currentQuestionIndex + 1, logText);
    }

    /**
     * これ以上選択肢を追加できるかチェックします。
     */
    private boolean canAddMoreChoices() {
        int maxSelectableChoices = configManager.getConfig().getMaxSelectableChoices();
        return selectedChoices.size() < maxSelectableChoices;
    }

    /**
     * 最大選択数超過のダイアログを表示します。
     */
    private void showMaxChoicesExceededDialog() {
        int maxSelectableChoices = configManager.getConfig().getMaxSelectableChoices();
        JOptionPane.showMessageDialog(this,
            "選択できる選択肢は最大" + maxSelectableChoices + "個までです",
            "選択数超過", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * 選択肢を追加します。
     */
    private void addChoice(String choiceText) {
        selectedChoices.add(choiceText);
        String logText = getChoiceLogText(choiceText, "");
        logger.logChoiceSelection(currentQuestionIndex + 1, logText);
    }

    /**
     * 理由入力の状態を更新します。
     */
    private void updateReasonInputState() {
        int minSelectableChoices = configManager.getConfig().getMinSelectableChoices();
        if (selectedChoices.size() >= minSelectableChoices) {
            enableReasonInput();
        } else {
            disableReasonInput(minSelectableChoices);
        }
    }

    /**
     * 理由入力を有効化します。
     */
    private void enableReasonInput() {
        resetReasonInput();
        statusLabel.setText(" ");
    }

    /**
     * 理由入力を無効化します。
     */
    private void disableReasonInput(int minSelectableChoices) {
        reasonTextArea.setEnabled(false);
        reasonTextArea.setText("");
        rewriteButton.setEnabled(false);
        nextButton.setEnabled(false);

        if (!selectedChoices.isEmpty()) {
            int remaining = minSelectableChoices - selectedChoices.size();
            statusLabel.setText(String.format(Constants.MSG_MIN_SELECTION_REQUIRED,
                minSelectableChoices, remaining));
            statusLabel.setForeground(Constants.COLOR_STATUS_WARNING);
        } else {
            statusLabel.setText(" ");
        }
    }
    
    private void updateChoiceButtonColors() {
        for (int i = 0; i < choiceButtons.size(); i++) {
            JButton button = choiceButtons.get(i);
            JPanel panel = (JPanel) button.getClientProperty("panel");
            JTextArea textArea = (JTextArea) button.getClientProperty("textArea");

            if (panel != null && textArea != null) {
                // choiceTextsのi番目のテキストがselectedChoicesに含まれているかチェック
                String choiceText = choiceTexts.get(i);
                if (selectedChoices.contains(choiceText)) {
                    panel.setBackground(Constants.COLOR_SELECTED);
                    textArea.setBackground(Constants.COLOR_SELECTED);
                    textArea.setForeground(Constants.COLOR_SELECTED_TEXT);
                } else {
                    panel.setBackground(Constants.COLOR_DEFAULT);
                    textArea.setBackground(Constants.COLOR_DEFAULT);
                    textArea.setForeground(Constants.COLOR_DEFAULT_TEXT);
                }
            }
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
        if (selectedChoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_CHOICE_SELECTED,
                "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 最小選択数のチェック
        int minSelectableChoices = configManager.getConfig().getMinSelectableChoices();
        if (selectedChoices.size() < minSelectableChoices) {
            JOptionPane.showMessageDialog(this,
                "最低" + minSelectableChoices + "個の選択肢を選択してください",
                "選択数不足", JOptionPane.ERROR_MESSAGE);
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

        // 組み合わせパターンを生成
        String choiceCombination = getChoiceCombination();

        // 回答を保存
        Response response = new Response(
            respondentId,
            FileUtils.getTimestamp(),
            currentQuestionIndex + 1,
            questions.get(currentQuestionIndex).getText(),
            new ArrayList<>(selectedChoices),
            reason,
            choiceCombination
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

        String filepath = getResponseFilepath();
        if (filepath != null) {
            saveAndShowResult(filepath);
        }

        dispose();
    }

    private String getResponseFilepath() {
        String filepath = configManager.getResponsePath(respondentId);

        if (filepath != null) {
            ensureDirectoryExists(filepath);
        } else {
            filepath = promptForFilepath();
        }

        return filepath;
    }

    private void ensureDirectoryExists(String filepath) {
        File responseDir = new File(filepath).getParentFile();
        if (responseDir != null && !responseDir.exists()) {
            responseDir.mkdirs();
        }
    }

    private String promptForFilepath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(Constants.RESPONSES_DIR));
        fileChooser.setSelectedFile(new File("responses_" + respondentId + ".csv"));

        int result = fileChooser.showSaveDialog(this);
        return result == JFileChooser.APPROVE_OPTION ?
               fileChooser.getSelectedFile().getAbsolutePath() : null;
    }

    private void saveAndShowResult(String filepath) {
        String outputFormat = configManager.getConfig().getOutputFormat();
        String baseFilepath = filepath.replace(".csv", "").replace(".json", "");

        boolean success = FileUtils.saveResponse(responses, baseFilepath, outputFormat);

        // 組み合わせパターンファイルを保存
        if (success && configManager.getConfig().isSaveCombinationPatterns()
            && configManager.getConfig().isUseChoiceLabels()) {
            String combinationPath = baseFilepath + "_combinations.csv";
            FileUtils.saveCombinationPatterns(responses, combinationPath);
        }

        if (success) {
            showSuccessMessage(baseFilepath, outputFormat);
        } else {
            JOptionPane.showMessageDialog(this, "保存に失敗しました",
                "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSuccessMessage(String baseFilepath, String outputFormat) {
        StringBuilder message = new StringBuilder("アンケートが完了しました。\n回答を保存しました:\n");

        if ("csv".equals(outputFormat) || "both".equals(outputFormat)) {
            message.append(baseFilepath).append(".csv\n");
        }
        if ("json".equals(outputFormat) || "both".equals(outputFormat)) {
            message.append(baseFilepath).append(".json\n");
        }

        JOptionPane.showMessageDialog(this, message.toString(), "完了",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 選択肢のインデックスをラベル（A, B, C...）に変換します。
     */
    private String getChoiceLabel(int index) {
        if (index < Constants.ALPHABET_SIZE) {
            return String.valueOf((char) ('A' + index));
        } else {
            // Z以降はAA, AB, AC... のように表記
            int first = (index / Constants.ALPHABET_SIZE) - 1;
            int second = index % Constants.ALPHABET_SIZE;
            return String.valueOf((char) ('A' + first)) + (char) ('A' + second);
        }
    }

    /**
     * 選択肢のログテキストを生成します。
     * 設定に応じてラベル（A, B, C...）または選択肢テキストを返します。
     */
    private String getChoiceLogText(String choiceText, String prefix) {
        if (configManager.getConfig().isUseChoiceLabels()) {
            Integer index = choiceIndexMap.get(choiceText);
            if (index != null) {
                return prefix + getChoiceLabel(index);
            }
        }
        return prefix + choiceText;
    }

    /**
     * 選択された選択肢の組み合わせパターンを生成します。
     * 例: "A,C,D"
     */
    private String getChoiceCombination() {
        if (!configManager.getConfig().isUseChoiceLabels()) {
            return "";
        }

        List<Integer> indices = selectedChoices.stream()
            .map(choiceIndexMap::get)
            .filter(Objects::nonNull)
            .sorted()
            .collect(Collectors.toList());

        return indices.stream()
            .map(this::getChoiceLabel)
            .collect(Collectors.joining(","));
    }
}
