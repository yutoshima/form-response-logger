package form.ui;

import form.Constants;
import form.model.Config;
import form.ui.settings.*;
import form.ui.util.FontUtility;
import form.util.ConfigManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 設定ウィンドウ
 * 各設定パネルを統合し、設定の読み込み・保存を管理します
 */
public class SettingsWindow extends JFrame {
    private ConfigManager configManager;
    private List<SettingsPanel> panels;

    public SettingsWindow() {
        configManager = new ConfigManager();
        setTitle(configManager.getConfig().getTitleSettings());
        setSize(Constants.SETTINGS_WINDOW_SIZE);
        setLocationRelativeTo(null);

        initializePanels();
        setupUI();
        loadAllSettings();
    }

    /**
     * すべての設定パネルを初期化します
     */
    private void initializePanels() {
        panels = new ArrayList<>();
        panels.add(new FileSettingsPanel(this));
        panels.add(new DataSettingsPanel(this));
        panels.add(new AudioSettingsPanel());
        panels.add(new ButtonLabelSettingsPanel());
        panels.add(new TitleSettingsPanel());
        panels.add(new LogActionSettingsPanel());
    }

    /**
     * UIを構築します
     */
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        mainPanel.setBorder(new EmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE,
            Constants.PADDING_LARGE, Constants.PADDING_LARGE));

        // タイトル
        JLabel titleLabel = new JLabel("設定", SwingConstants.CENTER);
        titleLabel.setFont(FontUtility.createSectionFont());
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 設定パネルをスクロールペインに配置
        JPanel settingsPanel = createSettingsPanel();
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(settingsPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ボタンパネル
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * すべての設定パネルを含むパネルを作成します
     */
    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < panels.size(); i++) {
            if (i > 0) {
                settingsPanel.add(Box.createVerticalStrut(20));
            }
            settingsPanel.add(panels.get(i).createPanel());
        }

        return settingsPanel;
    }

    /**
     * 保存・キャンセルボタンを含むパネルを作成します
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        JButton saveButton = new JButton("保存");
        saveButton.setFont(FontUtility.createButtonPrimaryFont());
        saveButton.setPreferredSize(Constants.BUTTON_SIZE_LARGE);
        saveButton.addActionListener(e -> saveAllSettings());

        JButton cancelButton = new JButton("キャンセル");
        cancelButton.setFont(FontUtility.createButtonSecondaryFont());
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE_LARGE);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /**
     * すべてのパネルから設定を読み込みます
     */
    private void loadAllSettings() {
        Config config = configManager.getConfig();
        for (SettingsPanel panel : panels) {
            panel.loadSettings(config);
        }
    }

    /**
     * すべてのパネルの設定を保存します
     */
    private void saveAllSettings() {
        Config config = configManager.getConfig();

        // バリデーション
        for (SettingsPanel panel : panels) {
            if (!panel.validate()) {
                return;
            }
        }

        // 保存 - 各パネルが新しいConfigインスタンスを返すので、順次更新していく
        for (SettingsPanel panel : panels) {
            Config updatedConfig = panel.saveSettings(config);
            if (updatedConfig == null) {
                // バリデーションまたは保存に失敗
                return;
            }
            config = updatedConfig;  // 次のパネルは更新されたConfigを受け取る
        }

        // 最終的なConfigをConfigManagerに設定して保存
        configManager.setConfig(config);
        configManager.saveConfig();

        JOptionPane.showMessageDialog(this, "設定を保存しました", "保存完了",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}
