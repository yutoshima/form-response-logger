package form.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLiteデータベース接続とスキーマ初期化を管理するサービス
 */
public class DatabaseService {
    private static final String DEFAULT_DB_PATH = "data/survey.db";
    private final String dbPath;
    private Connection connection;

    /**
     * デフォルトのデータベースパスでサービスを作成
     */
    public DatabaseService() {
        this(DEFAULT_DB_PATH);
    }

    /**
     * 指定されたデータベースパスでサービスを作成
     * @param dbPath データベースファイルのパス
     */
    public DatabaseService(String dbPath) {
        this.dbPath = dbPath;
    }

    /**
     * データベース接続を取得
     * @return データベース接続
     * @throws SQLException 接続に失敗した場合
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            connection.setAutoCommit(true);
        }
        return connection;
    }

    /**
     * データベーススキーマを初期化
     * @throws SQLException スキーマ作成に失敗した場合
     */
    public void initializeSchema() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            // questionsテーブル
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    text TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // choicesテーブル
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS choices (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    question_id INTEGER NOT NULL,
                    choice_text TEXT NOT NULL,
                    choice_order INTEGER NOT NULL,
                    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
                )
            """);

            // responsesテーブル
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS responses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    respondent_id TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    question_num INTEGER NOT NULL,
                    question_text TEXT,
                    reason TEXT,
                    choice_combination TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // selected_choicesテーブル
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS selected_choices (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    response_id INTEGER NOT NULL,
                    choice_text TEXT NOT NULL,
                    FOREIGN KEY (response_id) REFERENCES responses(id) ON DELETE CASCADE
                )
            """);

            // action_logsテーブル
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS action_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    respondent_id TEXT,
                    timestamp TEXT NOT NULL,
                    action_type TEXT NOT NULL,
                    details TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // インデックスの作成
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_responses_respondent ON responses(respondent_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_action_logs_respondent ON action_logs(respondent_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_choices_question ON choices(question_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_selected_choices_response ON selected_choices(response_id)");
        }
    }

    /**
     * データベース接続を閉じる
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // ログに記録するが、例外は投げない
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * データベースパスを取得
     * @return データベースパス
     */
    public String getDbPath() {
        return dbPath;
    }
}
