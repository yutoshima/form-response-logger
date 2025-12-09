package form.ui;

import form.Constants;
import form.model.Question;
import form.util.FileUtils;
import form.util.ConfigManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 問題作成エディタウィンドウ
 */
public class QuestionEditorWindow extends JFrame {
    private List<Question> questions = new ArrayList<>();
    private JTextArea questionTextArea;
    private List<JTextField> choiceFields = new ArrayList<>();
    private JPanel choicesPanel;
    private DefaultListModel<String> questionListModel;
    private JList<String> questionList;
    private ConfigManager configManager;

    // 編集モード関連
    private boolean isEditMode = false;
    private int editingIndex = -1;
    private JButton actionButton;
    private JLabel modeLabel;
    
    public QuestionEditorWindow() {
        configManager = new ConfigManager();
        setTitle(configManager.getConfig().getTitleQuestionEditor());
        setSize(Constants.EDITOR_WINDOW_SIZE);
        setLocationRelativeTo(null);

        setupUI();
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE,
            Constants.PADDING_LARGE, Constants.PADDING_LARGE));
        
        // タイトル
        JLabel titleLabel = new JLabel("問題作成エディタ", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_SECTION));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // スクロールパネル
        JScrollPane scrollPane = new JScrollPane(createEditorPanel());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createEditorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // 質問入力エリア
        panel.add(createQuestionInputArea());
        panel.add(Box.createVerticalStrut(Constants.VERTICAL_STRUT_MEDIUM));

        // 選択肢入力エリア
        panel.add(createChoicesArea());
        panel.add(Box.createVerticalStrut(Constants.VERTICAL_STRUT_MEDIUM));

        // 問題リストエリア
        panel.add(createQuestionListArea());
        panel.add(Box.createVerticalStrut(Constants.VERTICAL_STRUT_MEDIUM));
        
        // ボタンエリア
        panel.add(createButtonArea());
        
        return panel;
    }
    
    private JPanel createQuestionInputArea() {
        JPanel panel = new JPanel(new BorderLayout(Constants.EDITOR_COMPONENT_SPACING, Constants.EDITOR_COMPONENT_SPACING));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("質問文"),
            new EmptyBorder(Constants.EDITOR_PANEL_PADDING, Constants.EDITOR_PANEL_SIDE_PADDING,
                Constants.EDITOR_PANEL_SIDE_PADDING, Constants.EDITOR_PANEL_SIDE_PADDING)
        ));

        panel.add(createQuestionHeaderPanel(), BorderLayout.NORTH);
        panel.add(createQuestionTextAreaScrollPane(), BorderLayout.CENTER);

        return panel;
    }

    /**
     * 質問入力エリアのヘッダーパネルを作成します。
     */
    private JPanel createQuestionHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("質問文");
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        headerPanel.add(label, BorderLayout.WEST);

        modeLabel = new JLabel("【新規追加モード】");
        modeLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_LABEL));
        modeLabel.setForeground(Constants.COLOR_STATUS_SUCCESS);
        headerPanel.add(modeLabel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * 質問テキストエリアのスクロールパネルを作成します。
     */
    private JScrollPane createQuestionTextAreaScrollPane() {
        questionTextArea = new JTextArea(Constants.EDITOR_QUESTION_TEXTAREA_ROWS, Constants.EDITOR_QUESTION_TEXTAREA_COLS);
        questionTextArea.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        return new JScrollPane(questionTextArea);
    }
    
    private JPanel createChoicesArea() {
        JPanel panel = new JPanel(new BorderLayout(Constants.EDITOR_COMPONENT_SPACING, Constants.EDITOR_COMPONENT_SPACING));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("選択肢"),
            new EmptyBorder(Constants.EDITOR_PANEL_PADDING, Constants.EDITOR_PANEL_SIDE_PADDING,
                Constants.EDITOR_PANEL_SIDE_PADDING, Constants.EDITOR_PANEL_SIDE_PADDING)
        ));

        panel.add(createChoicesHeaderPanel(), BorderLayout.NORTH);

        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        initializeDefaultChoices();

        panel.add(createChoicesScrollPane(), BorderLayout.CENTER);

        return panel;
    }

    /**
     * 選択肢ヘッダーパネルを作成します。
     */
    private JPanel createChoicesHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("選択肢");
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        headerPanel.add(label, BorderLayout.WEST);

        JButton addChoiceButton = new JButton("選択肢を追加");
        addChoiceButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_SMALL));
        addChoiceButton.addActionListener(e -> addChoiceField());
        headerPanel.add(addChoiceButton, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * デフォルトの選択肢を初期化します。
     */
    private void initializeDefaultChoices() {
        int defaultChoices = configManager.getConfig().getDefaultChoices();
        for (int i = 0; i < defaultChoices; i++) {
            addChoiceField();
        }
    }

    /**
     * 選択肢スクロールパネルを作成します。
     */
    private JScrollPane createChoicesScrollPane() {
        JScrollPane scrollPane = new JScrollPane(choicesPanel);
        scrollPane.setPreferredSize(new Dimension(0, Constants.EDITOR_SCROLL_HEIGHT));
        return scrollPane;
    }
    
    private void addChoiceField() {
        if (!validateChoiceFieldLimit()) {
            return;
        }

        JPanel choicePanel = createChoicePanel();
        JTextField textField = createChoiceTextField();
        JButton removeButton = createRemoveButton(choicePanel, textField);

        assembleChoicePanel(choicePanel, textField, removeButton);
        addChoicePanelToUI(choicePanel);
    }

    /**
     * 選択肢フィールド数の上限チェックを行います。
     */
    private boolean validateChoiceFieldLimit() {
        if (choiceFields.size() >= Constants.MAX_CHOICES) {
            JOptionPane.showMessageDialog(this,
                "選択肢は最大" + Constants.MAX_CHOICES + "個までです");
            return false;
        }
        return true;
    }

    /**
     * 選択肢パネルを作成します。
     */
    private JPanel createChoicePanel() {
        JPanel panel = new JPanel(new BorderLayout(Constants.EDITOR_COMPONENT_SPACING, Constants.EDITOR_COMPONENT_SPACING));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.EDITOR_ROW_HEIGHT));
        return panel;
    }

    /**
     * 選択肢入力用のテキストフィールドを作成し、リストに追加します。
     */
    private JTextField createChoiceTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        choiceFields.add(textField);
        return textField;
    }

    /**
     * 選択肢削除ボタンを作成します。
     */
    private JButton createRemoveButton(JPanel choicePanel, JTextField textField) {
        JButton removeButton = new JButton("削除");
        removeButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_SMALL));
        removeButton.addActionListener(e -> removeChoiceField(choicePanel, textField));
        return removeButton;
    }

    /**
     * 選択肢フィールドを削除します。
     */
    private void removeChoiceField(JPanel choicePanel, JTextField textField) {
        choicesPanel.remove(choicePanel);
        choiceFields.remove(textField);
        refreshChoicesPanel();
    }

    /**
     * 選択肢パネルにコンポーネントを配置します。
     */
    private void assembleChoicePanel(JPanel choicePanel, JTextField textField, JButton removeButton) {
        choicePanel.add(new JLabel((choiceFields.size()) + ". "), BorderLayout.WEST);
        choicePanel.add(textField, BorderLayout.CENTER);
        choicePanel.add(removeButton, BorderLayout.EAST);
    }

    /**
     * 選択肢パネルをUIに追加します。
     */
    private void addChoicePanelToUI(JPanel choicePanel) {
        choicesPanel.add(choicePanel);
        refreshChoicesPanel();
    }

    /**
     * 選択肢パネルのUIを更新します。
     */
    private void refreshChoicesPanel() {
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }

    /**
     * ダブルクリックで問題を編集するためのマウスリスナーを作成します。
     */
    private java.awt.event.MouseAdapter createDoubleClickEditListener() {
        return new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = questionList.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        editQuestion(index);
                    }
                }
            }
        };
    }

    /**
     * 問題リスト操作用のボタンパネルを作成します。
     */
    private JPanel createListButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton editButton = new JButton("✎ 編集");
        editButton.addActionListener(e -> editSelectedQuestion());

        JButton upButton = new JButton("↑ 上へ");
        upButton.addActionListener(e -> moveQuestion(-1));

        JButton downButton = new JButton("↓ 下へ");
        downButton.addActionListener(e -> moveQuestion(1));

        JButton deleteButton = new JButton("✕ 削除");
        deleteButton.addActionListener(e -> deleteQuestion());

        panel.add(editButton);
        panel.add(upButton);
        panel.add(downButton);
        panel.add(deleteButton);

        return panel;
    }

    /**
     * 選択された問題を編集します。
     */
    private void editSelectedQuestion() {
        int index = questionList.getSelectedIndex();
        if (index >= 0) {
            editQuestion(index);
        }
    }

    private JPanel createQuestionListArea() {
        JPanel panel = new JPanel(new BorderLayout(Constants.EDITOR_COMPONENT_SPACING, Constants.EDITOR_COMPONENT_SPACING));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("問題リスト"),
            new EmptyBorder(Constants.EDITOR_PANEL_PADDING, Constants.EDITOR_PANEL_SIDE_PADDING,
                Constants.EDITOR_PANEL_SIDE_PADDING, Constants.EDITOR_PANEL_SIDE_PADDING)
        ));
        
        JLabel label = new JLabel("問題リスト");
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        panel.add(label, BorderLayout.NORTH);
        
        questionListModel = new DefaultListModel<>();
        questionList = new JList<>(questionListModel);
        questionList.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));

        // ダブルクリックで編集開始
        questionList.addMouseListener(createDoubleClickEditListener());

        JScrollPane scrollPane = new JScrollPane(questionList);
        scrollPane.setPreferredSize(new Dimension(0, Constants.EDITOR_QUESTION_LIST_HEIGHT));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // リスト操作ボタン
        panel.add(createListButtonPanel(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonArea() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            Constants.EDITOR_BUTTON_PANEL_SPACING, Constants.EDITOR_BUTTON_PANEL_SPACING));

        actionButton = createStyledButton("問題を追加", true);
        actionButton.addActionListener(e -> {
            if (isEditMode) {
                updateQuestion();
            } else {
                addQuestion();
            }
        });

        JButton cancelButton = createStyledButton("キャンセル", false);
        cancelButton.addActionListener(e -> cancelEdit());

        JButton saveButton = createStyledButton("保存", true);
        saveButton.addActionListener(e -> saveQuestions());

        JButton loadButton = createStyledButton("読み込み", false);
        loadButton.addActionListener(e -> loadQuestions());

        panel.add(actionButton);
        panel.add(cancelButton);
        panel.add(saveButton);
        panel.add(loadButton);

        return panel;
    }
    
    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        Font font = isPrimary ? 
            new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BUTTON) :
            new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_BUTTON);
        button.setFont(font);
        button.setPreferredSize(Constants.EDITOR_BUTTON_SIZE);
        button.setFocusPainted(false);
        return button;
    }
    
    private void addQuestion() {
        String questionText = questionTextArea.getText().trim();
        List<String> choices = collectChoices();

        if (!validateQuestionInput(questionText, choices)) {
            return;
        }

        Question question = new Question(questionText, choices);
        questions.add(question);
        questionListModel.addElement(questions.size() + ". " + questionText);
        clearForm();

        JOptionPane.showMessageDialog(this, Constants.MSG_QUESTION_ADDED);
    }

    private List<String> collectChoices() {
        List<String> choices = new ArrayList<>();
        for (JTextField field : choiceFields) {
            String choice = field.getText().trim();
            if (!choice.isEmpty()) {
                choices.add(choice);
            }
        }
        return choices;
    }

    private boolean validateQuestionInput(String questionText, List<String> choices) {
        if (questionText.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_QUESTION_TEXT);
            return false;
        }

        if (choices.size() < Constants.MIN_CHOICES) {
            JOptionPane.showMessageDialog(this, Constants.MSG_MIN_CHOICES);
            return false;
        }

        return true;
    }

    private void editQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        setEditMode(index);
        loadQuestionIntoForm(questions.get(index));
        updateModeUI();
        questionList.setSelectedIndex(index);
    }

    /**
     * 編集モードを設定します。
     */
    private void setEditMode(int index) {
        isEditMode = true;
        editingIndex = index;
    }

    /**
     * 問題をフォームに読み込みます。
     */
    private void loadQuestionIntoForm(Question question) {
        questionTextArea.setText(question.getText());
        clearChoiceFields();
        loadChoicesIntoFields(question.getChoices());
        refreshChoicesUI();
    }

    /**
     * 選択肢フィールドをクリアします。
     */
    private void clearChoiceFields() {
        choicesPanel.removeAll();
        choiceFields.clear();
    }

    /**
     * 選択肢をフィールドに読み込みます。
     */
    private void loadChoicesIntoFields(List<String> choices) {
        for (String choice : choices) {
            addChoiceField();
            choiceFields.getLast().setText(choice);
        }
    }

    /**
     * 選択肢UIをリフレッシュします。
     */
    private void refreshChoicesUI() {
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }

    private void updateQuestion() {
        String questionText = questionTextArea.getText().trim();
        List<String> choices = collectChoices();

        if (!validateQuestionInput(questionText, choices)) {
            return;
        }

        Question question = new Question(questionText, choices);
        questions.set(editingIndex, question);
        updateQuestionList();
        cancelEdit();

        JOptionPane.showMessageDialog(this, "問題を更新しました");
    }

    private void cancelEdit() {
        isEditMode = false;
        editingIndex = -1;
        clearForm();
        updateModeUI();
        questionList.clearSelection();
    }

    private void clearForm() {
        questionTextArea.setText("");
        for (JTextField field : choiceFields) {
            field.setText("");
        }
    }

    private void updateModeUI() {
        if (isEditMode) {
            modeLabel.setText("【編集モード - 問題 " + (editingIndex + 1) + " を編集中】");
            modeLabel.setForeground(Constants.COLOR_STATUS_WARNING);
            actionButton.setText("問題を更新");
        } else {
            modeLabel.setText("【新規追加モード】");
            modeLabel.setForeground(Constants.COLOR_STATUS_SUCCESS);
            actionButton.setText("問題を追加");
        }
    }
    
    private void moveQuestion(int direction) {
        int selectedIndex = questionList.getSelectedIndex();
        if (selectedIndex < 0) return;
        
        int newIndex = selectedIndex + direction;
        if (newIndex < 0 || newIndex >= questions.size()) return;
        
        // リストを入れ替え
        Question temp = questions.get(selectedIndex);
        questions.set(selectedIndex, questions.get(newIndex));
        questions.set(newIndex, temp);
        
        updateQuestionList();
        questionList.setSelectedIndex(newIndex);
    }
    
    private void deleteQuestion() {
        int selectedIndex = questionList.getSelectedIndex();
        if (selectedIndex < 0) return;
        
        int result = JOptionPane.showConfirmDialog(this,
            "選択した問題を削除しますか？",
            "確認",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            questions.remove(selectedIndex);
            updateQuestionList();
        }
    }
    
    private void updateQuestionList() {
        questionListModel.clear();
        for (int i = 0; i < questions.size(); i++) {
            questionListModel.addElement((i + 1) + ". " + questions.get(i).getText());
        }
    }
    
    private void saveQuestions() {
        if (!validateQuestionsExist()) {
            return;
        }

        JFileChooser fileChooser = createQuestionFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            handleSaveFile(fileChooser.getSelectedFile());
        }
    }

    /**
     * 保存対象の問題が存在するか検証します。
     */
    private boolean validateQuestionsExist() {
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_QUESTIONS);
            return false;
        }
        return true;
    }

    /**
     * ファイル保存処理を行います。
     */
    private void handleSaveFile(File file) {
        String filepath = ensureFileExtension(file.getAbsolutePath());
        boolean success = saveQuestionsToFile(filepath);

        showSaveResult(success, filepath);
    }

    /**
     * ファイルパスに適切な拡張子を付与します。
     */
    private String ensureFileExtension(String filepath) {
        if (!filepath.endsWith(".csv") && !filepath.endsWith(".json")) {
            return filepath + ".csv";
        }
        return filepath;
    }

    /**
     * 問題をファイルに保存します。
     */
    private boolean saveQuestionsToFile(String filepath) {
        if (filepath.endsWith(".json")) {
            return FileUtils.saveQuestionsToJSON(questions, filepath);
        } else {
            return FileUtils.saveQuestionsToCSV(questions, filepath);
        }
    }

    /**
     * 保存結果をダイアログで表示します。
     */
    private void showSaveResult(boolean success, String filepath) {
        if (success) {
            JOptionPane.showMessageDialog(this, "保存しました: " + filepath);
        } else {
            JOptionPane.showMessageDialog(this, "保存に失敗しました",
                "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadQuestions() {
        JFileChooser fileChooser = createQuestionFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            handleLoadFile(fileChooser.getSelectedFile());
        }
    }

    /**
     * 問題ファイル選択用のファイルチューザーを作成します。
     */
    private JFileChooser createQuestionFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(Constants.QUESTIONS_DIR));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV/JSONファイル (*.csv, *.json)", "csv", "json"));
        return fileChooser;
    }

    /**
     * ファイル読み込み処理を行います。
     */
    private void handleLoadFile(File file) {
        List<Question> loadedQuestions = FileUtils.loadQuestions(file.getAbsolutePath());

        if (!loadedQuestions.isEmpty()) {
            applyLoadedQuestions(loadedQuestions);
        } else {
            showLoadError();
        }
    }

    /**
     * 読み込んだ問題をアプリケーションに適用します。
     */
    private void applyLoadedQuestions(List<Question> loadedQuestions) {
        questions = loadedQuestions;
        updateQuestionList();
        JOptionPane.showMessageDialog(this,
            loadedQuestions.size() + "個の問題を読み込みました");
    }

    /**
     * 読み込み失敗時のエラーを表示します。
     */
    private void showLoadError() {
        JOptionPane.showMessageDialog(this, "問題を読み込めませんでした",
            "エラー", JOptionPane.ERROR_MESSAGE);
    }
}
