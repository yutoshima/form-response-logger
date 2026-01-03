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
    private JPanel imagesPanel;
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
        String filepath = getQuestionsFilepath();

        if (filepath == null) {
            dispose();
            return;
        }

        questions = FileUtils.loadQuestions(filepath);

        if (!validateLoadedQuestions()) {
            dispose();
            return;
        }

        setupUI();
        displayQuestion();
    }

    /**
     * 質問ファイルのパスを取得します。
     */
    private String getQuestionsFilepath() {
        String filepath = configManager.getQuestionsPath();

        if (filepath != null && new File(filepath).exists()) {
            return filepath;
        }

        return promptForQuestionsFile();
    }

    /**
     * 質問ファイルの選択ダイアログを表示します。
     */
    private String promptForQuestionsFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV/JSONファイル (*.csv, *.json)", "csv", "json"));

        int result = fileChooser.showOpenDialog(this);

        return result == JFileChooser.APPROVE_OPTION ?
               fileChooser.getSelectedFile().getAbsolutePath() : null;
    }

    /**
     * 読み込んだ質問を検証します。
     */
    private boolean validateLoadedQuestions() {
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "問題を読み込めませんでした",
                "エラー", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
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

        addQuestionDisplay(contentPanel);
        addImagesPanel(contentPanel);
        addChoicesPanel(contentPanel);
        addReasonSection(contentPanel);
        contentPanel.add(Box.createVerticalGlue());

        return createScrollableContentPanel(contentPanel);
    }

    /**
     * 質問表示エリアをコンテンツパネルに追加します。
     */
    private void addQuestionDisplay(JPanel contentPanel) {
        boolean useHtml = configManager.getConfig().isUseHtmlRendering();

        if (useHtml) {
            createHtmlQuestionDisplay();
        } else {
            createTextQuestionDisplay();
        }

        contentPanel.add(questionScrollPane);
    }

    /**
     * HTML形式の質問表示を作成します。
     */
    private void createHtmlQuestionDisplay() {
        questionEditorPane = new JEditorPane();
        questionEditorPane.setContentType("text/html");
        questionEditorPane.setEditable(false);
        questionEditorPane.setFocusable(false);
        questionEditorPane.setOpaque(false);
        questionEditorPane.setBorder(new EmptyBorder(0, 0, Constants.PADDING_LARGE, 0));
        questionEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        questionEditorPane.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_SUBTITLE));

        questionScrollPane = new JScrollPane(questionEditorPane);
        questionScrollPane.setBorder(null);
        questionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.QUESTION_SCROLL_HEIGHT_HTML));
    }

    /**
     * テキスト形式の質問表示を作成します。
     */
    private void createTextQuestionDisplay() {
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
    }

    /**
     * 画像パネルをコンテンツパネルに追加します。
     */
    private void addImagesPanel(JPanel contentPanel) {
        imagesPanel = new JPanel(new GridLayout(1, 2, Constants.PADDING_MEDIUM, 0));
        imagesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagesPanel.setVisible(false);
        contentPanel.add(imagesPanel);
        contentPanel.add(Box.createVerticalStrut(Constants.PADDING_MEDIUM));
    }

    /**
     * 選択肢パネルをコンテンツパネルに追加します。
     */
    private void addChoicesPanel(JPanel contentPanel) {
        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        choicesPanel.setBorder(BorderFactory.createEmptyBorder(Constants.PADDING_MEDIUM, 0, Constants.PADDING_MEDIUM, 0));
        contentPanel.add(choicesPanel);
    }

    /**
     * 理由入力セクションをコンテンツパネルに追加します。
     */
    private void addReasonSection(JPanel contentPanel) {
        contentPanel.add(Box.createVerticalStrut(Constants.PADDING_EXTRA_LARGE));
        contentPanel.add(createReasonPanel());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        statusLabel.setForeground(Constants.COLOR_STATUS_ERROR);
        statusLabel.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, 0, 0));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusLabel);
    }

    /**
     * スクロール可能なコンテンツパネルを作成します。
     */
    private JScrollPane createScrollableContentPanel(JPanel contentPanel) {
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createReasonPanel() {
        JPanel reasonPanel = new JPanel(new BorderLayout(Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        reasonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reasonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.REASON_PANEL_HEIGHT));

        reasonPanel.add(createReasonLabel(), BorderLayout.NORTH);
        createReasonTextArea();
        reasonPanel.add(createReasonScrollPane(), BorderLayout.CENTER);
        reasonPanel.add(createRewriteButtonPanel(), BorderLayout.SOUTH);

        return reasonPanel;
    }

    /**
     * 理由入力ラベルを作成します。
     */
    private JLabel createReasonLabel() {
        JLabel reasonLabel = new JLabel("選択した理由を記入してください");
        reasonLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        return reasonLabel;
    }

    /**
     * 理由入力テキストエリアを作成します。
     */
    private void createReasonTextArea() {
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
    }

    /**
     * 理由入力スクロールパネルを作成します。
     */
    private JScrollPane createReasonScrollPane() {
        JScrollPane reasonScrollPane = new JScrollPane(reasonTextArea);
        reasonScrollPane.setPreferredSize(new Dimension(0, Constants.REASON_TEXT_AREA_HEIGHT));
        reasonScrollPane.setMinimumSize(new Dimension(0, Constants.REASON_TEXT_AREA_HEIGHT));
        reasonScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.REASON_TEXT_AREA_HEIGHT));
        return reasonScrollPane;
    }

    /**
     * 選び直しボタンパネルを作成します。
     */
    private JPanel createRewriteButtonPanel() {
        rewriteButton = new JButton(configManager.getConfig().getButtonReselect());
        rewriteButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BUTTON));
        rewriteButton.setBackground(Constants.COLOR_GRAY);
        rewriteButton.setEnabled(false);
        rewriteButton.addActionListener(e -> rewriteReason());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(rewriteButton);
        return buttonPanel;
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
        displayImages(question);
        displayChoices(question);
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
        String cssStyle = buildCssStyle();

        if (!isHtmlFormat(questionText)) {
            return wrapInHtmlWithStyle(questionText, cssStyle);
        }

        return ensureStyleInHtml(questionText, cssStyle);
    }

    /**
     * CSSスタイルを構築します。
     */
    private String buildCssStyle() {
        return "body { font-family: '" + Constants.FONT_FAMILY +
               "'; font-size: " + Constants.FONT_SIZE_SUBTITLE + "pt; font-weight: normal; }";
    }

    /**
     * テキストがHTML形式かチェックします。
     */
    private boolean isHtmlFormat(String text) {
        return text.trim().toLowerCase().startsWith("<html");
    }

    /**
     * テキストをHTMLとスタイルでラップします。
     */
    private String wrapInHtmlWithStyle(String text, String cssStyle) {
        return "<html><head><style>" + cssStyle + "</style></head><body>" +
               text + "</body></html>";
    }

    /**
     * HTMLテキストにスタイルが含まれていることを確認します。
     */
    private String ensureStyleInHtml(String htmlText, String cssStyle) {
        if (htmlText.toLowerCase().contains("<style>")) {
            return htmlText;
        }

        return insertStyleIntoHtml(htmlText, cssStyle);
    }

    /**
     * HTMLテキストにスタイルを挿入します。
     */
    private String insertStyleIntoHtml(String htmlText, String cssStyle) {
        String styleTag = "<style>" + cssStyle + "</style>";

        if (htmlText.toLowerCase().contains("<head>")) {
            return htmlText.replaceFirst("(?i)<head>", "<head>" + styleTag);
        }

        return htmlText.replaceFirst("(?i)<html>",
            "<html><head>" + styleTag + "</head>");
    }

    /**
     * 画像を表示します（modelImageとstudentImageが指定されている場合）。
     */
    private void displayImages(Question question) {
        imagesPanel.removeAll();
        imagesPanel.setVisible(false);

        String modelImagePath = question.getModelImage();
        String studentImagePath = question.getStudentImage();

        if (modelImagePath != null && studentImagePath != null) {
            JPanel modelPanel = createImagePanel("模範解答", modelImagePath);
            JPanel studentPanel = createImagePanel("学習者の回答", studentImagePath);

            if (modelPanel != null && studentPanel != null) {
                imagesPanel.add(modelPanel);
                imagesPanel.add(studentPanel);
                imagesPanel.setVisible(true);
                imagesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
            }
        }

        imagesPanel.revalidate();
        imagesPanel.repaint();
    }

    /**
     * 画像パネルを作成します。
     */
    private JPanel createImagePanel(String label, String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.err.println("画像ファイルが見つかりません: " + imagePath);
                return null;
            }

            ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());
            Image scaledImage = scaleImage(imageIcon.getImage(), 480);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

            JPanel panel = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                               Constants.PADDING_SMALL, Constants.PADDING_SMALL)
            ));

            JLabel titleLabel = new JLabel(label, SwingConstants.CENTER);
            titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_LABEL));
            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(imageLabel, BorderLayout.CENTER);

            return panel;
        } catch (Exception e) {
            System.err.println("画像の読み込みに失敗しました: " + imagePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 画像を指定された幅にスケーリングします。
     */
    private Image scaleImage(Image originalImage, int targetWidth) {
        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);

        if (originalWidth <= 0 || originalHeight <= 0) {
            return originalImage;
        }

        double aspectRatio = (double) originalHeight / originalWidth;
        int targetHeight = (int) (targetWidth * aspectRatio);

        return originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
    }

    private void displayChoices(Question question) {
        clearChoiceState();

        List<String> choices = getChoicesForQuestion(question);
        List<String> preparedChoices = prepareChoices(choices);
        buildChoiceIndexMap(preparedChoices);

        int columns = configManager.getConfig().getChoiceColumns();
        renderChoiceGrid(preparedChoices, columns);
    }

    /**
     * 質問から選択肢リストを取得します（scale型の場合は自動生成）。
     */
    private List<String> getChoicesForQuestion(Question question) {
        if ("scale".equals(question.getType()) && question.getMin() != null && question.getMax() != null) {
            return generateScaleChoices(question.getMin(), question.getMax());
        }
        return question.getChoices();
    }

    /**
     * スケール選択肢を生成します（min から max まで）。
     */
    private List<String> generateScaleChoices(int min, int max) {
        List<String> choices = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            choices.add(String.valueOf(i));
        }
        return choices;
    }

    /**
     * 選択肢の状態をクリアします。
     */
    private void clearChoiceState() {
        choicesPanel.removeAll();
        choiceButtons.clear();
        choiceTexts.clear();
        choiceIndexMap.clear();
    }

    /**
     * 選択肢を準備します（必要に応じてシャッフル）。
     */
    private List<String> prepareChoices(List<String> originalChoices) {
        List<String> choices = new ArrayList<>(originalChoices);

        if (configManager.getConfig().isRandomizeChoices()) {
            Collections.shuffle(choices);
        }

        return choices;
    }

    /**
     * 選択肢のインデックスマップを構築します。
     */
    private void buildChoiceIndexMap(List<String> choices) {
        for (int i = 0; i < choices.size(); i++) {
            choiceIndexMap.put(choices.get(i), i);
        }
    }

    /**
     * 選択肢をグリッド形式で描画します。
     */
    private void renderChoiceGrid(List<String> choices, int columns) {
        JPanel currentRow = null;

        for (int i = 0; i < choices.size(); i++) {
            if (i % columns == 0) {
                currentRow = addNewChoiceRow(i);
            }

            String choice = choices.get(i);
            choiceTexts.add(choice);
            createChoiceButton(choice, i, currentRow);
        }

        fillEmptyChoiceSlots(currentRow, columns, choices.size());
    }

    /**
     * 新しい選択肢行を追加します。
     */
    private JPanel addNewChoiceRow(int index) {
        if (index > 0) {
            choicesPanel.add(Box.createVerticalStrut(Constants.PADDING_MEDIUM));
        }

        int columns = configManager.getConfig().getChoiceColumns();
        JPanel row = createChoiceRow(columns);
        choicesPanel.add(row);

        return row;
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
        if (!validateNextQuestionInput()) {
            return;
        }

        String reason = reasonTextArea.getText().trim();
        logger.logReasonText(currentQuestionIndex + 1, reason);

        saveCurrentResponse(reason);
        moveToNextQuestion();
        displayQuestion();
    }

    /**
     * 次の問題へ進む前の入力を検証します。
     */
    private boolean validateNextQuestionInput() {
        if (selectedChoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_CHOICE_SELECTED,
                "エラー", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!validateMinimumSelectionCount()) {
            return false;
        }

        if (!validateReasonInput()) {
            return false;
        }

        return true;
    }

    /**
     * 最小選択数を検証します。
     */
    private boolean validateMinimumSelectionCount() {
        int minSelectableChoices = configManager.getConfig().getMinSelectableChoices();

        if (selectedChoices.size() < minSelectableChoices) {
            JOptionPane.showMessageDialog(this,
                "最低" + minSelectableChoices + "個の選択肢を選択してください",
                "選択数不足", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * 理由入力を検証します。
     */
    private boolean validateReasonInput() {
        String reason = reasonTextArea.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_REASON,
                "エラー", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * 現在の回答を保存します。
     */
    private void saveCurrentResponse(String reason) {
        String choiceCombination = getChoiceCombination();

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
    }

    /**
     * 次の問題へ移動します。
     */
    private void moveToNextQuestion() {
        int oldIndex = currentQuestionIndex;
        currentQuestionIndex++;

        if (currentQuestionIndex < questions.size()) {
            logger.logNextQuestion(oldIndex + 1, currentQuestionIndex + 1);
        }
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
