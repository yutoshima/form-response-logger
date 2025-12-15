package form.ui;

import form.Constants;
import form.ui.util.FontUtility;
import form.util.ConfigManager;

import javax.swing.*;
import java.awt.*;

/**
 * メインウィンドウ
 */
public class MainWindow extends JFrame {

    private ConfigManager configManager;

    public MainWindow() {
        configManager = new ConfigManager();
        setTitle(configManager.getConfig().getTitleMain());
        setSize(Constants.MAIN_WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupUI();
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createTitleLabel(), BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * タイトルラベルを作成します。
     */
    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel(configManager.getConfig().getTitleMain(), SwingConstants.CENTER);
        titleLabel.setFont(FontUtility.createTitleFont());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        return titleLabel;
    }

    /**
     * ボタンパネルを作成します。
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createButtonConstraints();

        addMainButtons(buttonPanel, gbc);

        return buttonPanel;
    }

    /**
     * ボタン配置用の制約を作成します。
     */
    private GridBagConstraints createButtonConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    /**
     * メインボタンをパネルに追加します。
     */
    private void addMainButtons(JPanel buttonPanel, GridBagConstraints gbc) {
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
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FontUtility.createCustomFont(Font.PLAIN, Constants.FONT_SIZE_SUBTITLE));
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
