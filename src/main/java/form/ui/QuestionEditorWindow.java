package form.ui;

import form.Constants;
import form.ui.editor.QuestionEditorLogic;
import form.ui.editor.QuestionEditorState;
import form.ui.editor.QuestionEditorUIBuilder;
import form.util.ConfigManager;

import javax.swing.*;
import java.awt.*;

/**
 * 問題作成エディタウィンドウ（リファクタリング版）
 * UIBuilder、State、Logicを使用して責任を分離
 */
public class QuestionEditorWindow extends JFrame {
    private final ConfigManager configManager;
    private final QuestionEditorState state;
    private final QuestionEditorUIBuilder uiBuilder;
    private final QuestionEditorLogic logic;

    public QuestionEditorWindow() {
        configManager = new ConfigManager();
        state = new QuestionEditorState();
        uiBuilder = new QuestionEditorUIBuilder(configManager);
        logic = new QuestionEditorLogic(state, this);

        setTitle(configManager.getConfig().getTitleQuestionEditor());
        setSize(Constants.EDITOR_WINDOW_SIZE);
        setLocationRelativeTo(null);

        setupUI();
        initializeDefaultChoices();
    }

    /**
     * UIをセットアップします。
     */
    private void setupUI() {
        add(uiBuilder.buildMainPanel());
        setupButtonPanel();
        setupListButtonPanel();
        setupEventHandlers();
    }

    /**
     * ボタンパネルをセットアップします。
     */
    private void setupButtonPanel() {
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            Constants.EDITOR_BUTTON_PANEL_SPACING, Constants.EDITOR_BUTTON_PANEL_SPACING));

        var actionButton = uiBuilder.createStyledButton("問題を追加", true);
        actionButton.addActionListener(e -> handleActionButton());
        uiBuilder.setActionButton(actionButton);

        var cancelButton = uiBuilder.createStyledButton("キャンセル", false);
        cancelButton.addActionListener(e -> cancelEdit());

        var saveButton = uiBuilder.createStyledButton("保存", true);
        saveButton.addActionListener(e -> saveQuestions());

        var loadButton = uiBuilder.createStyledButton("読み込み", false);
        loadButton.addActionListener(e -> loadQuestions());

        buttonPanel.add(actionButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        // メインパネルに追加
        ((JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView())
            .add(buttonPanel);
    }

    /**
     * リスト操作ボタンパネルをセットアップします。
     */
    private void setupListButtonPanel() {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        var editButton = new JButton("✎ 編集");
        editButton.addActionListener(e -> editSelectedQuestion());

        var upButton = new JButton("↑ 上へ");
        upButton.addActionListener(e -> moveQuestion(-1));

        var downButton = new JButton("↓ 下へ");
        downButton.addActionListener(e -> moveQuestion(1));

        var deleteButton = new JButton("✕ 削除");
        deleteButton.addActionListener(e -> deleteQuestion());

        panel.add(editButton);
        panel.add(upButton);
        panel.add(downButton);
        panel.add(deleteButton);

        // 問題リストエリアの最後のコンポーネントとして追加
        var editorPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0))
            .getViewport().getView();
        var listPanel = (JPanel) editorPanel.getComponent(editorPanel.getComponentCount() - 2);
        listPanel.add(panel, BorderLayout.SOUTH);
    }

    /**
     * イベントハンドラーをセットアップします。
     */
    private void setupEventHandlers() {
        // ダブルクリックで編集開始
        uiBuilder.getQuestionList().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    var index = uiBuilder.getQuestionList().locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        editQuestion(index);
                    }
                }
            }
        });

        // 選択肢ヘッダーに追加ボタンを配置
        var choicesHeader = findChoicesHeaderPanel();
        if (choicesHeader != null) {
            var addChoiceButton = new JButton("選択肢を追加");
            addChoiceButton.addActionListener(e -> addChoiceField());
            choicesHeader.add(addChoiceButton, BorderLayout.EAST);
        }
    }

    /**
     * 選択肢ヘッダーパネルを見つけます。
     */
    private JPanel findChoicesHeaderPanel() {
        var editorPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0))
            .getViewport().getView();
        var choicesArea = (JPanel) editorPanel.getComponent(2);
        return (JPanel) choicesArea.getComponent(0);
    }

    /**
     * デフォルトの選択肢を初期化します。
     */
    private void initializeDefaultChoices() {
        var defaultChoices = configManager.getConfig().getDefaultChoices();
        for (int i = 0; i < defaultChoices; i++) {
            addChoiceField();
        }
    }

    /**
     * 選択肢フィールドを追加します。
     */
    private void addChoiceField() {
        if (uiBuilder.getChoiceFields().size() >= Constants.MAX_CHOICES) {
            JOptionPane.showMessageDialog(this,
                "選択肢は最大" + Constants.MAX_CHOICES + "個までです");
            return;
        }

        var textField = new JTextField();
        uiBuilder.getChoiceFields().add(textField);

        var choicePanel = uiBuilder.createChoicePanel(textField, () -> removeChoiceField(textField));
        uiBuilder.getChoicesPanel().add(choicePanel);
        refreshChoicesPanel();
    }

    /**
     * 選択肢フィールドを削除します。
     */
    private void removeChoiceField(JTextField textField) {
        var index = uiBuilder.getChoiceFields().indexOf(textField);
        if (index >= 0) {
            uiBuilder.getChoicesPanel().remove(index);
            uiBuilder.getChoiceFields().remove(index);
            refreshChoicesPanel();
        }
    }

    /**
     * 選択肢パネルをリフレッシュします。
     */
    private void refreshChoicesPanel() {
        uiBuilder.getChoicesPanel().revalidate();
        uiBuilder.getChoicesPanel().repaint();
    }

    /**
     * アクションボタンの処理を行います。
     */
    private void handleActionButton() {
        var questionText = uiBuilder.getQuestionTextArea().getText().trim();
        var choices = logic.collectChoices(uiBuilder.getChoiceFields());

        var success = state.isEditMode()
            ? logic.updateQuestion(questionText, choices)
            : logic.addQuestion(questionText, choices);

        if (success) {
            updateQuestionList();
            if (!state.isEditMode()) {
                clearForm();
            } else {
                cancelEdit();
            }
        }
    }

    /**
     * 選択された問題を編集します。
     */
    private void editSelectedQuestion() {
        var index = uiBuilder.getQuestionList().getSelectedIndex();
        if (index >= 0) {
            editQuestion(index);
        }
    }

    /**
     * 問題を編集します。
     */
    private void editQuestion(int index) {
        var question = state.getQuestion(index);
        if (question == null) return;

        state.startEditMode(index);
        loadQuestionIntoForm(question);
        updateModeUI();
        uiBuilder.getQuestionList().setSelectedIndex(index);
    }

    /**
     * 問題をフォームに読み込みます。
     */
    private void loadQuestionIntoForm(form.model.Question question) {
        uiBuilder.getQuestionTextArea().setText(question.getText());

        // 選択肢をクリアして再構築
        uiBuilder.getChoicesPanel().removeAll();
        uiBuilder.getChoiceFields().clear();

        for (var choice : question.getChoices()) {
            var textField = new JTextField(choice);
            uiBuilder.getChoiceFields().add(textField);
            var choicePanel = uiBuilder.createChoicePanel(textField, () -> removeChoiceField(textField));
            uiBuilder.getChoicesPanel().add(choicePanel);
        }

        refreshChoicesPanel();
    }

    /**
     * 編集をキャンセルします。
     */
    private void cancelEdit() {
        state.cancelEditMode();
        clearForm();
        updateModeUI();
        uiBuilder.getQuestionList().clearSelection();
    }

    /**
     * フォームをクリアします。
     */
    private void clearForm() {
        uiBuilder.getQuestionTextArea().setText("");
        for (var field : uiBuilder.getChoiceFields()) {
            field.setText("");
        }
    }

    /**
     * モードUIを更新します。
     */
    private void updateModeUI() {
        if (state.isEditMode()) {
            uiBuilder.getModeLabel().setText("【編集モード - 問題 " +
                (state.getEditingIndex() + 1) + " を編集中】");
            uiBuilder.getModeLabel().setForeground(Constants.COLOR_STATUS_WARNING);
            uiBuilder.getActionButton().setText("問題を更新");
        } else {
            uiBuilder.getModeLabel().setText("【新規追加モード】");
            uiBuilder.getModeLabel().setForeground(Constants.COLOR_STATUS_SUCCESS);
            uiBuilder.getActionButton().setText("問題を追加");
        }
    }

    /**
     * 問題を移動します。
     */
    private void moveQuestion(int direction) {
        var selectedIndex = uiBuilder.getQuestionList().getSelectedIndex();
        if (logic.moveQuestion(selectedIndex, direction)) {
            updateQuestionList();
            uiBuilder.getQuestionList().setSelectedIndex(selectedIndex + direction);
        }
    }

    /**
     * 問題を削除します。
     */
    private void deleteQuestion() {
        var selectedIndex = uiBuilder.getQuestionList().getSelectedIndex();
        if (logic.deleteQuestion(selectedIndex)) {
            updateQuestionList();
        }
    }

    /**
     * 問題リストを更新します。
     */
    private void updateQuestionList() {
        uiBuilder.getQuestionListModel().clear();
        var questions = state.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            uiBuilder.getQuestionListModel().addElement((i + 1) + ". " + questions.get(i).getText());
        }
    }

    /**
     * 問題を保存します。
     */
    private void saveQuestions() {
        logic.saveQuestions();
    }

    /**
     * 問題を読み込みます。
     */
    private void loadQuestions() {
        if (logic.loadQuestions()) {
            updateQuestionList();
        }
    }
}
