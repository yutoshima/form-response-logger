package com.study.form.ui;

import com.study.form.Constants;
import com.study.form.util.ConfigManager;

import javax.swing.*;
import java.awt.*;

/**
 * メインウィンドウ
 */
public class MainWindow extends JFrame {

    private ConfigManager configManager;

    public MainWindow() {
        configManager = new ConfigManager();
        setTitle("研究用アンケートシステム");
        setSize(Constants.MAIN_WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setupUI();
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // タイトル
        JLabel titleLabel = new JLabel("研究用アンケートシステム", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_TITLE));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ボタンを作成（設定から文言を取得）
        JButton createButton = createStyledButton(configManager.getConfig().getButtonCreateQuestions());
        createButton.addActionListener(e -> openQuestionEditor());

        JButton answerButton = createStyledButton(configManager.getConfig().getButtonTakeSurvey());
        answerButton.addActionListener(e -> openSurveyInterface());
        
        JButton settingsButton = createStyledButton("⚙ 設定");
        settingsButton.setBackground(Constants.COLOR_GRAY);
        settingsButton.addActionListener(e -> openSettings());
        
        JButton exitButton = createStyledButton("終了");
        exitButton.setBackground(Constants.COLOR_GRAY);
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(createButton, gbc);
        buttonPanel.add(answerButton, gbc);
        buttonPanel.add(settingsButton, gbc);
        buttonPanel.add(exitButton, gbc);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_SUBTITLE));
        button.setPreferredSize(new Dimension(300, 50));
        button.setFocusPainted(false);
        return button;
    }
    
    private void openQuestionEditor() {
        QuestionEditorWindow editor = new QuestionEditorWindow();
        editor.setVisible(true);
    }
    
    private void openSurveyInterface() {
        SurveyInterfaceWindow survey = new SurveyInterfaceWindow();
        survey.setVisible(true);
    }
    
    private void openSettings() {
        SettingsWindow settings = new SettingsWindow();
        settings.setVisible(true);
    }
}
