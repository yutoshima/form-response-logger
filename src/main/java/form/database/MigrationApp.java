package form.database;

import java.io.File;
import java.sql.SQLException;

/**
 * CSVからSQLiteへのデータ移行を実行するコマンドラインツール
 */
public class MigrationApp {

    public static void main(String[] args) {
        System.out.println("=== CSV to SQLite Migration Tool ===\n");

        // デフォルトのパス
        String dbPath = "data/survey.db";
        String questionsDir = "data/questions";
        String responsesDir = "data/responses";
        String logsDir = "data/logs";

        // コマンドライン引数からパスを取得（オプション）
        if (args.length >= 1) dbPath = args[0];
        if (args.length >= 2) questionsDir = args[1];
        if (args.length >= 3) responsesDir = args[2];
        if (args.length >= 4) logsDir = args[3];

        System.out.println("Database path: " + dbPath);
        System.out.println("Questions directory: " + questionsDir);
        System.out.println("Responses directory: " + responsesDir);
        System.out.println("Logs directory: " + logsDir);
        System.out.println();

        DatabaseService dbService = new DatabaseService(dbPath);
        CSVMigrationTool migrationTool = new CSVMigrationTool(dbService);

        try {
            // データベーススキーマを初期化
            System.out.println("Initializing database schema...");
            dbService.initializeSchema();
            System.out.println("✓ Database schema initialized\n");

            // 質問ファイルを移行
            File questionsFolder = new File(questionsDir);
            if (questionsFolder.exists() && questionsFolder.isDirectory()) {
                System.out.println("Migrating questions from: " + questionsDir);
                File[] questionFiles = questionsFolder.listFiles((dir, name) -> name.endsWith(".csv"));

                if (questionFiles != null && questionFiles.length > 0) {
                    int totalQuestions = 0;
                    for (File file : questionFiles) {
                        try {
                            int count = migrationTool.migrateQuestionsFromCSV(file.getAbsolutePath());
                            totalQuestions += count;
                            System.out.println("  - " + file.getName() + ": " + count + " questions");
                        } catch (Exception e) {
                            System.err.println("  ✗ Error migrating " + file.getName() + ": " + e.getMessage());
                        }
                    }
                    System.out.println("✓ Total questions migrated: " + totalQuestions + "\n");
                } else {
                    System.out.println("  No CSV files found in questions directory\n");
                }
            } else {
                System.out.println("  Questions directory not found: " + questionsDir + "\n");
            }

            // 回答ファイルを移行
            File responsesFolder = new File(responsesDir);
            if (responsesFolder.exists() && responsesFolder.isDirectory()) {
                System.out.println("Migrating responses from: " + responsesDir);
                try {
                    int count = migrationTool.migrateAllResponsesFromDirectory(responsesDir);
                    System.out.println("✓ Total responses migrated: " + count + "\n");
                } catch (Exception e) {
                    System.err.println("✗ Error migrating responses: " + e.getMessage() + "\n");
                }
            } else {
                System.out.println("  Responses directory not found: " + responsesDir + "\n");
            }

            // アクションログファイルを移行
            File logsFolder = new File(logsDir);
            if (logsFolder.exists() && logsFolder.isDirectory()) {
                System.out.println("Migrating action logs from: " + logsDir);
                try {
                    int count = migrationTool.migrateAllActionLogsFromDirectory(logsDir);
                    System.out.println("✓ Total action logs migrated: " + count + "\n");
                } catch (Exception e) {
                    System.err.println("✗ Error migrating action logs: " + e.getMessage() + "\n");
                }
            } else {
                System.out.println("  Logs directory not found: " + logsDir + "\n");
            }

            System.out.println("=== Migration completed successfully! ===");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbService.close();
        }
    }
}
