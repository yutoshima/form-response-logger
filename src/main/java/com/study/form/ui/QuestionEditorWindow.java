package com.study.form.ui;

import com.study.form.Constants;
import com.study.form.model.Question;
import com.study.form.util.FileUtils;
import com.study.form.util.ConfigManager;

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
        setTitle("問題作成エディタ");
        setSize(Constants.EDITOR_WINDOW_SIZE);
        setLocationRelativeTo(null);
        
        configManager = new ConfigManager();
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
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("質問文"),
            new EmptyBorder(10, 15, 15, 15)
        ));

        // ヘッダーパネル（モード表示を追加）
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("質問文");
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        headerPanel.add(label, BorderLayout.WEST);

        modeLabel = new JLabel("【新規追加モード】");
        modeLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_LABEL));
        modeLabel.setForeground(Constants.COLOR_STATUS_SUCCESS);
        headerPanel.add(modeLabel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        questionTextArea = new JTextArea(4, 40);
        questionTextArea.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(questionTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createChoicesArea() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("選択肢"),
            new EmptyBorder(10, 15, 15, 15)
        ));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("選択肢");
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        headerPanel.add(label, BorderLayout.WEST);
        
        JButton addChoiceButton = new JButton("選択肢を追加");
        addChoiceButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_SMALL));
        addChoiceButton.addActionListener(e -> addChoiceField());
        headerPanel.add(addChoiceButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        
        // デフォルトの選択肢数（設定から取得）
        int defaultChoices = configManager.getConfig().getDefaultChoices();
        for (int i = 0; i < defaultChoices; i++) {
            addChoiceField();
        }
        
        JScrollPane scrollPane = new JScrollPane(choicesPanel);
        scrollPane.setPreferredSize(new Dimension(0, Constants.EDITOR_SCROLL_HEIGHT));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addChoiceField() {
        if (choiceFields.size() >= Constants.MAX_CHOICES) {
            JOptionPane.showMessageDialog(this, 
                "選択肢は最大" + Constants.MAX_CHOICES + "個までです");
            return;
        }
        
        JPanel choicePanel = new JPanel(new BorderLayout(5, 5));
        choicePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.EDITOR_ROW_HEIGHT));
        
        JTextField textField = new JTextField();
        textField.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        choiceFields.add(textField);
        
        JButton removeButton = new JButton("削除");
        removeButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_SMALL));
        removeButton.addActionListener(e -> {
            choicesPanel.remove(choicePanel);
            choiceFields.remove(textField);
            choicesPanel.revalidate();
            choicesPanel.repaint();
        });
        
        choicePanel.add(new JLabel((choiceFields.size()) + ". "), BorderLayout.WEST);
        choicePanel.add(textField, BorderLayout.CENTER);
        choicePanel.add(removeButton, BorderLayout.EAST);
        
        choicesPanel.add(choicePanel);
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }
    
    private JPanel createQuestionListArea() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("問題リスト"),
            new EmptyBorder(10, 15, 15, 15)
        ));
        
        JLabel label = new JLabel("問題リスト");
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        panel.add(label, BorderLayout.NORTH);
        
        questionListModel = new DefaultListModel<>();
        questionList = new JList<>(questionListModel);
        questionList.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));

        // ダブルクリックで編集開始
        questionList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = questionList.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        editQuestion(index);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(questionList);
        scrollPane.setPreferredSize(new Dimension(0, Constants.EDITOR_QUESTION_LIST_HEIGHT));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // リスト操作ボタン
        JPanel listButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton editButton = new JButton("✎ 編集");
        editButton.addActionListener(e -> {
            int index = questionList.getSelectedIndex();
            if (index >= 0) {
                editQuestion(index);
            }
        });

        JButton upButton = new JButton("↑ 上へ");
        upButton.addActionListener(e -> moveQuestion(-1));

        JButton downButton = new JButton("↓ 下へ");
        downButton.addActionListener(e -> moveQuestion(1));

        JButton deleteButton = new JButton("✕ 削除");
        deleteButton.addActionListener(e -> deleteQuestion());

        listButtonPanel.add(editButton);
        listButtonPanel.add(upButton);
        listButtonPanel.add(downButton);
        listButtonPanel.add(deleteButton);
        
        panel.add(listButtonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonArea() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

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

        if (questionText.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_QUESTION_TEXT);
            return;
        }

        List<String> choices = new ArrayList<>();
        for (JTextField field : choiceFields) {
            String choice = field.getText().trim();
            if (!choice.isEmpty()) {
                choices.add(choice);
            }
        }

        if (choices.size() < Constants.MIN_CHOICES) {
            JOptionPane.showMessageDialog(this, Constants.MSG_MIN_CHOICES);
            return;
        }

        Question question = new Question(questionText, choices);
        questions.add(question);

        // リストに追加
        questionListModel.addElement((questions.size()) + ". " + questionText);

        // 入力をクリア
        clearForm();

        JOptionPane.showMessageDialog(this, Constants.MSG_QUESTION_ADDED);
    }

    private void editQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        isEditMode = true;
        editingIndex = index;

        Question question = questions.get(index);

        // フォームに読み込む
        questionTextArea.setText(question.getText());

        // 既存の選択肢フィールドをクリア
        choicesPanel.removeAll();
        choiceFields.clear();

        // 選択肢を読み込む
        for (String choice : question.getChoices()) {
            addChoiceField();
            choiceFields.get(choiceFields.size() - 1).setText(choice);
        }

        choicesPanel.revalidate();
        choicesPanel.repaint();

        // UIを更新
        updateModeUI();

        // 問題リストの選択を更新
        questionList.setSelectedIndex(index);
    }

    private void updateQuestion() {
        String questionText = questionTextArea.getText().trim();

        if (questionText.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_QUESTION_TEXT);
            return;
        }

        List<String> choices = new ArrayList<>();
        for (JTextField field : choiceFields) {
            String choice = field.getText().trim();
            if (!choice.isEmpty()) {
                choices.add(choice);
            }
        }

        if (choices.size() < Constants.MIN_CHOICES) {
            JOptionPane.showMessageDialog(this, Constants.MSG_MIN_CHOICES);
            return;
        }

        // 問題を更新
        Question question = new Question(questionText, choices);
        questions.set(editingIndex, question);

        // リストを更新
        updateQuestionList();

        // フォームをクリアして新規追加モードに戻る
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
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, Constants.MSG_NO_QUESTIONS);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(Constants.QUESTIONS_DIR));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV/JSONファイル (*.csv, *.json)", "csv", "json"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filepath = file.getAbsolutePath();
            
            // 拡張子チェック
            if (!filepath.endsWith(".csv") && !filepath.endsWith(".json")) {
                filepath += ".csv";
            }
            
            boolean success;
            if (filepath.endsWith(".json")) {
                success = FileUtils.saveQuestionsToJSON(questions, filepath);
            } else {
                success = FileUtils.saveQuestionsToCSV(questions, filepath);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "保存しました: " + filepath);
            } else {
                JOptionPane.showMessageDialog(this, "保存に失敗しました", 
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadQuestions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(Constants.QUESTIONS_DIR));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV/JSONファイル (*.csv, *.json)", "csv", "json"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            List<Question> loadedQuestions = FileUtils.loadQuestions(file.getAbsolutePath());
            
            if (!loadedQuestions.isEmpty()) {
                questions = loadedQuestions;
                updateQuestionList();
                JOptionPane.showMessageDialog(this, 
                    loadedQuestions.size() + "個の問題を読み込みました");
            } else {
                JOptionPane.showMessageDialog(this, "問題を読み込めませんでした", 
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
