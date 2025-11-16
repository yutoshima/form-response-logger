package com.study.form;

import java.awt.*;
import java.io.File;

/**
 * 定数定義クラス
 * アプリケーション全体で使用する定数を一元管理
 */
public class Constants {
    
    // ディレクトリ構造
    public static final String BASE_DIR = System.getProperty("user.dir");
    public static final String DATA_DIR = BASE_DIR + File.separator + "data";
    public static final String QUESTIONS_DIR = DATA_DIR + File.separator + "questions";
    public static final String RESPONSES_DIR = DATA_DIR + File.separator + "responses";
    public static final String LOGS_DIR = DATA_DIR + File.separator + "logs";
    
    // ファイル名
    public static final String CONFIG_FILE = "config.json";
    public static final String DEFAULT_QUESTIONS_FILE = "sample_questions.csv";
    
    // ウィンドウサイズ
    public static final Dimension MAIN_WINDOW_SIZE = new Dimension(900, 700);
    public static final Dimension EDITOR_WINDOW_SIZE = new Dimension(1000, 750);
    public static final Dimension SETTINGS_WINDOW_SIZE = new Dimension(800, 650);
    public static final Dimension SURVEY_WINDOW_SIZE = new Dimension(900, 800);
    public static final Dimension PARTICIPANT_INFO_WINDOW_SIZE = new Dimension(400, 220);
    
    // フォント
    public static final String FONT_FAMILY = "Yu Gothic";
    public static final int FONT_SIZE_TITLE = 28;
    public static final int FONT_SIZE_SECTION = 24;
    public static final int FONT_SIZE_SUBTITLE = 16;
    public static final int FONT_SIZE_NORMAL = 14;
    public static final int FONT_SIZE_BUTTON = 14;
    public static final int FONT_SIZE_LABEL = 12;
    public static final int FONT_SIZE_SMALL = 11;
    
    // 色設定
    public static final Color COLOR_SELECTED = new Color(76, 175, 80);      // 明るい緑
    public static final Color COLOR_SELECTED_HOVER = new Color(67, 160, 71); // やや濃い緑
    public static final Color COLOR_SELECTED_TEXT = Color.WHITE;              // 選択時の文字色
    public static final Color COLOR_DEFAULT = new Color(224, 224, 224);      // 明るいグレー
    public static final Color COLOR_DEFAULT_HOVER = new Color(189, 189, 189); // やや濃いグレー
    public static final Color COLOR_DEFAULT_TEXT = new Color(33, 33, 33);     // デフォルトの文字色
    public static final Color COLOR_GRAY = new Color(97, 97, 97);
    public static final Color COLOR_GRAY_HOVER = new Color(77, 77, 77);
    public static final Color COLOR_STATUS_WARNING = new Color(255, 152, 0);  // オレンジ
    public static final Color COLOR_STATUS_SUCCESS = new Color(76, 175, 80);  // 緑
    public static final Color COLOR_STATUS_ERROR = new Color(244, 67, 54);    // 赤

    // UI要素のサイズ
    public static final Dimension BUTTON_SIZE_LARGE = new Dimension(120, 40);
    public static final Dimension BUTTON_SIZE_MEDIUM = new Dimension(100, 35);
    public static final Dimension CHOICE_BUTTON_MIN_SIZE = new Dimension(100, 50);
    public static final Dimension CHOICE_BUTTON_PREFERRED_SIZE = new Dimension(500, 50);
    public static final int CHOICE_BUTTON_HEIGHT = 50;
    public static final int TEXT_FIELD_HEIGHT = 30;

    // 設定画面UI要素のサイズ
    public static final Dimension SETTINGS_LABEL_SIZE = new Dimension(200, 25);
    public static final Dimension SETTINGS_COMPONENT_SIZE = new Dimension(200, 25);
    public static final int SETTINGS_ROW_HEIGHT = 35;

    // パディングとスペーシング
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_MEDIUM_LARGE = 15;
    public static final int PADDING_LARGE = 20;
    public static final int PADDING_EXTRA_LARGE = 30;
    public static final int VERTICAL_STRUT_SMALL = 10;
    public static final int VERTICAL_STRUT_MEDIUM = 15;
    public static final int VERTICAL_STRUT_LARGE = 20;

    // エディター画面のサイズ
    public static final int EDITOR_SCROLL_HEIGHT = 200;
    public static final int EDITOR_QUESTION_LIST_HEIGHT = 150;
    public static final int EDITOR_ROW_HEIGHT = 35;
    public static final Dimension EDITOR_BUTTON_SIZE = new Dimension(150, 40);
    
    // バリデーション設定
    public static final int MIN_CHOICES = 2;
    public static final int MAX_CHOICES = 10;
    public static final int DEFAULT_CHOICES = 4;
    
    // ログ設定
    public static final String LOG_ACTION_CHOICE_SELECTION = "選択肢選択";
    public static final String LOG_ACTION_REASON_START = "理由入力開始";
    public static final String LOG_ACTION_REASON_TEXT = "理由入力内容";
    public static final String LOG_ACTION_REASON_REWRITE = "理由書き直し";
    public static final String LOG_ACTION_QUESTION_MOVE = "問題移動";
    public static final String LOG_ACTION_SUBMIT = "アンケート送信";
    public static final int LOG_TEXT_PREVIEW_LENGTH = 100;
    
    // メッセージ
    public static final String MSG_NO_QUESTIONS = "保存する問題がありません";
    public static final String MSG_NO_QUESTION_TEXT = "質問文を入力してください";
    public static final String MSG_MIN_CHOICES = "最低" + MIN_CHOICES + "つの選択肢を入力してください";
    public static final String MSG_QUESTION_ADDED = "問題を追加しました";
    public static final String MSG_NO_CHOICE_SELECTED = "選択肢を選んでください";
    public static final String MSG_NO_REASON = "理由を記入してください";
    public static final String MSG_CANNOT_CHANGE_CHOICE = "理由を書き始めた後は選択肢を変更できません。\n「理由を書き直す」ボタンを押してください。";
    public static final String MSG_CHANGE_DISABLED_STATUS = "⚠ 理由を書き直してから選択肢を変更してください";
    public static final String MSG_REASON_STARTED_STATUS = "理由を書き直すまで選択肢は変更できません";
    public static final String MSG_CAN_CHANGE_STATUS = "選択肢を変更できます";
}
