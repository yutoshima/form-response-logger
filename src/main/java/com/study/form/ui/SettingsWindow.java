package com.study.form.ui;

import com.study.form.Constants;
import com.study.form.model.Config;
import com.study.form.util.ConfigManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * 設定ウィンドウ
 */
public class SettingsWindow extends JFrame {
    private ConfigManager configManager;
    
    private JTextField questionsFileField;
    private JTextField logDirField;
    private JTextField logFormatField;
    private JTextField responseDirField;
    private JTextField responseFormatField;
    private JTextField participantNameField;
    private JTextField participantIdField;

    private JComboBox<String> outputFormatCombo;
    private JComboBox<Integer> defaultChoicesCombo;
    private JComboBox<Integer> choiceColumnsCombo;
    private JTextField logSequenceField;
    private JTextField responseSequenceField;
    private JCheckBox autoSaveCheckBox;
    private JCheckBox useParticipantInfoCheckBox;
    private JCheckBox useHtmlRenderingCheckBox;
    private JComboBox<String> contentWidthCombo;

    public SettingsWindow() {
        setTitle("設定");
        setSize(Constants.SETTINGS_WINDOW_SIZE);
        setLocationRelativeTo(null);
        
        configManager = new ConfigManager();
        
        setupUI();
        loadCurrentSettings();
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        mainPanel.setBorder(new EmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE,
            Constants.PADDING_LARGE, Constants.PADDING_LARGE));
        
        // タイトル
        JLabel titleLabel = new JLabel("設定", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_SECTION));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // スクロールパネル
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        
        // ファイル設定
        settingsPanel.add(createSection("ファイル設定"));
        settingsPanel.add(createFileSettings());
        settingsPanel.add(Box.createVerticalStrut(20));
        
        // データ設定
        settingsPanel.add(createSection("データ設定"));
        settingsPanel.add(createDataSettings());
        settingsPanel.add(Box.createVerticalStrut(20));
        
        JScrollPane scrollPane = new JScrollPane(settingsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // ボタンパネル
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JLabel createSection(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * 保存・キャンセルボタンを含むパネルを作成します。
     *
     * @return ボタンパネル
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        JButton saveButton = new JButton("保存");
        saveButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BUTTON));
        saveButton.setPreferredSize(Constants.BUTTON_SIZE_LARGE);
        saveButton.addActionListener(e -> saveSettings());

        JButton cancelButton = new JButton("キャンセル");
        cancelButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_BUTTON));
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE_LARGE);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }
    
    private JPanel createFileSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("ファイル設定"),
            new EmptyBorder(10, 15, 15, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        questionsFileField = new JTextField(30);
        logDirField = new JTextField(30);
        logFormatField = new JTextField(30);
        responseDirField = new JTextField(30);
        responseFormatField = new JTextField(30);
        participantNameField = new JTextField(30);
        participantIdField = new JTextField(30);

        panel.add(createFieldRow("被験者名:", participantNameField, false));
        panel.add(createFieldRow("被験者ID:", participantIdField, false));
        panel.add(createFieldRow("問題ファイル:", questionsFileField, true));
        panel.add(createFieldRow("ログ出力ディレクトリ:", logDirField, true));
        panel.add(createFieldRow("ログファイル名フォーマット:", logFormatField, false));
        panel.add(createFieldRow("回答出力ディレクトリ:", responseDirField, true));
        panel.add(createFieldRow("回答ファイル名フォーマット:", responseFormatField, false));
        
        // ヘルプテキスト
        JLabel helpLabel = new JLabel("<html><i>使用可能な変数: {date}, {time}, {participant_name}, {participant_id}, {sequence}</i></html>");
        helpLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_SMALL));
        helpLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(helpLabel);
        
        return panel;
    }
    
    private JPanel createFieldRow(String labelText, JTextField field, boolean hasButton) {
        JPanel row = new JPanel(new BorderLayout(5, 5));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row.setBorder(new EmptyBorder(5, 0, 5, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        label.setPreferredSize(new Dimension(200, 25));
        row.add(label, BorderLayout.WEST);
        
        field.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        row.add(field, BorderLayout.CENTER);
        
        if (hasButton) {
            JButton browseButton = new JButton("参照");
            browseButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_SMALL));
            if (labelText.contains("ファイル")) {
                browseButton.addActionListener(e -> browseFile(field));
            } else {
                browseButton.addActionListener(e -> browseDirectory(field));
            }
            row.add(browseButton, BorderLayout.EAST);
        }
        
        return row;
    }
    
    private void browseFile(JTextField field) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV/JSONファイル", "csv", "json"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void browseDirectory(JTextField field) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(field.getText()));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private JPanel createDataSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("データ設定"),
            new EmptyBorder(10, 15, 15, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] outputFormats = {"csv", "json", "both"};
        Integer[] defaultChoicesOptions = {2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] choiceColumnsOptions = {1, 2, 3, 4};

        outputFormatCombo = new JComboBox<>(outputFormats);
        defaultChoicesCombo = new JComboBox<>(defaultChoicesOptions);
        choiceColumnsCombo = new JComboBox<>(choiceColumnsOptions);
        logSequenceField = new JTextField(10);
        responseSequenceField = new JTextField(10);
        autoSaveCheckBox = new JCheckBox("有効にすると設定に基づいて自動保存");
        useParticipantInfoCheckBox = new JCheckBox("有効にすると被験者名・IDを入力");
        useHtmlRenderingCheckBox = new JCheckBox("有効にするとHTMLで表示（短文のみ）、無効にするとプレーンテキストで表示（長文対応）");

        // コンテンツ横幅の選択肢（Bootstrap風サイズ）
        String[] widthOptions = {"小 (540px)", "中 (720px)", "大 (960px)", "特大 (1140px)"};
        contentWidthCombo = new JComboBox<>(widthOptions);

        panel.add(createComboRow("出力形式:", outputFormatCombo));
        panel.add(createIntComboRow("デフォルト選択肢数:", defaultChoicesCombo));
        panel.add(createIntComboRow("選択肢の列数:", choiceColumnsCombo));
        panel.add(createComboRow("コンテンツ横幅:", contentWidthCombo));
        panel.add(createFieldRow("ログ連番（次回）:", logSequenceField, false));
        panel.add(createFieldRow("回答連番（次回）:", responseSequenceField, false));
        panel.add(createCheckBoxRow("自動保存:", autoSaveCheckBox));
        panel.add(createCheckBoxRow("被験者情報を使用:", useParticipantInfoCheckBox));
        panel.add(createCheckBoxRow("HTML表示:", useHtmlRenderingCheckBox));

        return panel;
    }
    
    /**
     * 汎用的な設定行を作成します。
     *
     * @param labelText ラベルテキスト
     * @param component 配置するコンポーネント
     * @param setComponentSize コンポーネントのサイズを設定するかどうか
     * @return 設定行のパネル
     */
    private JPanel createSettingRow(String labelText, JComponent component, boolean setComponentSize) {
        JPanel row = new JPanel(new BorderLayout(Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Constants.SETTINGS_ROW_HEIGHT));
        row.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ラベルの作成
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        label.setPreferredSize(Constants.SETTINGS_LABEL_SIZE);
        row.add(label, BorderLayout.WEST);

        // コンポーネントの設定
        component.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        if (setComponentSize) {
            component.setPreferredSize(Constants.SETTINGS_COMPONENT_SIZE);
        }

        // コンポーネントを左寄せパネルに配置
        JPanel componentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        componentPanel.add(component);
        row.add(componentPanel, BorderLayout.CENTER);

        return row;
    }

    private JPanel createIntComboRow(String labelText, JComboBox<Integer> combo) {
        return createSettingRow(labelText, combo, true);
    }

    private JPanel createComboRow(String labelText, JComboBox<String> combo) {
        return createSettingRow(labelText, combo, true);
    }

    private JPanel createCheckBoxRow(String labelText, JCheckBox checkBox) {
        return createSettingRow(labelText, checkBox, false);
    }
    
    private void loadCurrentSettings() {
        Config config = configManager.getConfig();

        participantNameField.setText(config.getParticipantName() != null ? config.getParticipantName() : "");
        participantIdField.setText(config.getParticipantId() != null ? config.getParticipantId() : "");

        // 問題ファイルのフルパスを表示（ディレクトリとファイル名を結合）
        String questionsPath = configManager.getQuestionsPath();
        questionsFileField.setText(questionsPath != null ? questionsPath : "");

        logDirField.setText(config.getLogDirectory() != null ? config.getLogDirectory() : "");
        logFormatField.setText(config.getLogNameFormat() != null ? config.getLogNameFormat() : "");
        responseDirField.setText(config.getResponseDirectory() != null ? config.getResponseDirectory() : "");
        responseFormatField.setText(config.getResponseNameFormat() != null ? config.getResponseNameFormat() : "");

        outputFormatCombo.setSelectedItem(config.getOutputFormat());
        defaultChoicesCombo.setSelectedItem(config.getDefaultChoices());
        choiceColumnsCombo.setSelectedItem(config.getChoiceColumns());
        logSequenceField.setText(String.valueOf(config.getLogSequence()));
        responseSequenceField.setText(String.valueOf(config.getResponseSequence()));
        autoSaveCheckBox.setSelected(config.isAutoSave());
        useParticipantInfoCheckBox.setSelected(config.isUseParticipantInfo());
        useHtmlRenderingCheckBox.setSelected(config.isUseHtmlRendering());

        // 横幅設定の読み込み
        int width = config.getContentWidth();
        if (width <= 540) {
            contentWidthCombo.setSelectedIndex(0);
        } else if (width <= 720) {
            contentWidthCombo.setSelectedIndex(1);
        } else if (width <= 960) {
            contentWidthCombo.setSelectedIndex(2);
        } else {
            contentWidthCombo.setSelectedIndex(3);
        }
    }
    
    private void saveSettings() {
        Config config = configManager.getConfig();

        config.setParticipantName(participantNameField.getText());
        config.setParticipantId(participantIdField.getText());

        // 問題ファイルのフルパスをディレクトリとファイル名に分割
        String questionsPath = questionsFileField.getText();
        if (questionsPath != null && !questionsPath.trim().isEmpty()) {
            File questionsFile = new File(questionsPath);
            String directory = questionsFile.getParent();
            String filename = questionsFile.getName();
            config.setQuestionsDirectory(directory);
            config.setQuestionsFile(filename);
            System.out.println("問題ファイル設定: ディレクトリ=" + directory + ", ファイル名=" + filename);
        }

        config.setLogDirectory(logDirField.getText());
        config.setLogNameFormat(logFormatField.getText());
        config.setResponseDirectory(responseDirField.getText());
        config.setResponseNameFormat(responseFormatField.getText());

        config.setOutputFormat((String) outputFormatCombo.getSelectedItem());
        config.setDefaultChoices((Integer) defaultChoicesCombo.getSelectedItem());
        config.setChoiceColumns((Integer) choiceColumnsCombo.getSelectedItem());

        // 連番の設定（数値検証付き）
        try {
            int logSeq = Integer.parseInt(logSequenceField.getText());
            int responseSeq = Integer.parseInt(responseSequenceField.getText());
            if (logSeq < 1 || responseSeq < 1) {
                JOptionPane.showMessageDialog(this, "連番は1以上の数値を入力してください",
                    "入力エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }
            config.setLogSequence(logSeq);
            config.setResponseSequence(responseSeq);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "連番には数値を入力してください",
                "入力エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }

        config.setAutoSave(autoSaveCheckBox.isSelected());
        config.setUseParticipantInfo(useParticipantInfoCheckBox.isSelected());
        config.setUseHtmlRendering(useHtmlRenderingCheckBox.isSelected());

        // 横幅設定の保存
        int selectedWidthIndex = contentWidthCombo.getSelectedIndex();
        int[] widthValues = {540, 720, 960, 1140};
        config.setContentWidth(widthValues[selectedWidthIndex]);

        configManager.saveConfig();

        JOptionPane.showMessageDialog(this, "設定を保存しました", "保存完了",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}
