package form.ui;

import form.Constants;
import form.util.ConfigManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 被験者情報入力ダイアログ
 *
 * <p>このクラスは、アンケート開始前に被験者の名前とIDを入力するための
 * モーダルダイアログを提供します。</p>
 *
 * <p>主な機能：</p>
 * <ul>
 *   <li>被験者名の入力</li>
 *   <li>被験者IDの入力</li>
 *   <li>前回入力した情報の自動読み込み</li>
 *   <li>入力情報の設定ファイルへの保存</li>
 *   <li>入力バリデーション（空白チェック）</li>
 * </ul>
 *
 * <p>使用方法：</p>
 * <pre>{@code
 * ParticipantInfoWindow dialog = new ParticipantInfoWindow(parentFrame);
 * dialog.setVisible(true);
 * if (dialog.isConfirmed()) {
 *     // ユーザーが「確定」を押した場合の処理
 * } else {
 *     // ユーザーが「キャンセル」を押した場合の処理
 * }
 * }</pre>
 *
 * @author Survey App Development Team
 * @version 1.0
 * @since 1.0
 */
public class ParticipantInfoWindow extends JDialog {
    private ConfigManager configManager;
    private JTextField nameField;
    private JTextField idField;
    private boolean confirmed = false;

    public ParticipantInfoWindow(Frame parent) {
        super(parent, "被験者情報入力", true);
        setSize(Constants.PARTICIPANT_INFO_WINDOW_SIZE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        configManager = new ConfigManager();

        setupUI();
        loadCurrentInfo();
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // タイトル
        JLabel titleLabel = new JLabel("被験者情報を入力してください", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_NORMAL));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 入力フォーム
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        // 被験者名
        JLabel nameLabel = new JLabel("被験者名:");
        nameLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        nameField = new JTextField(20);
        nameField.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));

        // 被験者ID
        JLabel idLabel = new JLabel("被験者ID:");
        idLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));
        idField = new JTextField(20);
        idField.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(idLabel);
        formPanel.add(idField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton confirmButton = new JButton("確定");
        confirmButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BUTTON));
        confirmButton.setPreferredSize(Constants.BUTTON_SIZE_MEDIUM);
        confirmButton.addActionListener(e -> confirmInput());

        JButton cancelButton = new JButton("キャンセル");
        cancelButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_BUTTON));
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE_MEDIUM);
        cancelButton.addActionListener(e -> cancelInput());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadCurrentInfo() {
        String name = configManager.getConfig().getParticipantName();
        String id = configManager.getConfig().getParticipantId();

        if (name != null && !name.isEmpty()) {
            nameField.setText(name);
        }
        if (id != null && !id.isEmpty()) {
            idField.setText(id);
        }
    }

    private void confirmInput() {
        String name = nameField.getText().trim();
        String id = idField.getText().trim();

        if (!validateInput(name, id)) {
            return;
        }

        saveParticipantInfo(name, id);
        confirmed = true;
        dispose();
    }

    /**
     * 入力内容を検証します。
     */
    private boolean validateInput(String name, String id) {
        if (!validateField(name, "被験者名")) {
            return false;
        }

        if (!validateField(id, "被験者ID")) {
            return false;
        }

        return true;
    }

    /**
     * フィールドが空でないか検証します。
     */
    private boolean validateField(String value, String fieldName) {
        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                fieldName + "を入力してください",
                "入力エラー",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 被験者情報を設定に保存します。
     */
    private void saveParticipantInfo(String name, String id) {
        configManager.getConfig().setParticipantName(name);
        configManager.getConfig().setParticipantId(id);
        configManager.saveConfig();
    }

    private void cancelInput() {
        confirmed = false;
        dispose();
    }

    /**
     * ユーザーが入力を確定したかどうかを返します。
     *
     * <p>このメソッドは、ダイアログが閉じられた後に呼び出して、
     * ユーザーが「確定」ボタンを押したか、「キャンセル」ボタンを押したかを
     * 判定するために使用します。</p>
     *
     * @return ユーザーが「確定」を押した場合は{@code true}、
     *         「キャンセル」を押した場合は{@code false}
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}
