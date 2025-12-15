package form.ui;

import form.Constants;
import form.ui.util.FontUtility;
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

        mainPanel.add(createTitleLabel(), BorderLayout.NORTH);
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * タイトルラベルを作成します。
     */
    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("被験者情報を入力してください", SwingConstants.CENTER);
        titleLabel.setFont(FontUtility.createNormalBoldFont());
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        return titleLabel;
    }

    /**
     * 入力フォームパネルを作成します。
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel nameLabel = createFormLabel("被験者名:");
        nameField = createFormTextField();

        JLabel idLabel = createFormLabel("被験者ID:");
        idField = createFormTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(idLabel);
        formPanel.add(idField);

        return formPanel;
    }

    /**
     * フォームラベルを作成します。
     */
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontUtility.createLabelFont());
        return label;
    }

    /**
     * フォームテキストフィールドを作成します。
     */
    private JTextField createFormTextField() {
        JTextField field = new JTextField(20);
        field.setFont(FontUtility.createLabelFont());
        return field;
    }

    /**
     * ボタンパネルを作成します。
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton confirmButton = new JButton("確定");
        confirmButton.setFont(FontUtility.createButtonPrimaryFont());
        confirmButton.setPreferredSize(Constants.BUTTON_SIZE_MEDIUM);
        confirmButton.addActionListener(e -> confirmInput());

        JButton cancelButton = new JButton("キャンセル");
        cancelButton.setFont(FontUtility.createButtonSecondaryFont());
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE_MEDIUM);
        cancelButton.addActionListener(e -> cancelInput());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
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
