package form.database;

import form.model.Question;
import form.model.Response;
import form.util.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * CSVファイルからSQLiteデータベースへのデータ移行ツール
 */
public class CSVMigrationTool {
    private final DatabaseService dbService;
    private final QuestionDAO questionDAO;
    private final ResponseDAO responseDAO;
    private final ActionLogDAO actionLogDAO;

    public CSVMigrationTool(DatabaseService dbService) {
        this.dbService = dbService;
        this.questionDAO = new QuestionDAO(dbService);
        this.responseDAO = new ResponseDAO(dbService);
        this.actionLogDAO = new ActionLogDAO(dbService);
    }

    /**
     * 質問CSVファイルをデータベースに移行
     * @param csvFilePath CSVファイルパス
     * @return 移行された質問数
     * @throws IOException ファイル読み込みエラー
     * @throws SQLException データベースエラー
     */
    public int migrateQuestionsFromCSV(String csvFilePath) throws IOException, SQLException {
        List<Question> questions = FileUtils.loadQuestions(csvFilePath);
        int count = 0;

        for (Question question : questions) {
            questionDAO.save(question);
            count++;
        }

        return count;
    }

    /**
     * 回答CSVファイルをデータベースに移行
     * @param csvFilePath CSVファイルパス
     * @return 移行された回答数
     * @throws IOException ファイル読み込みエラー
     * @throws SQLException データベースエラー
     */
    public int migrateResponsesFromCSV(String csvFilePath) throws IOException, SQLException {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // ヘッダー行をスキップ
                }

                Response response = parseResponseLine(line);
                if (response != null) {
                    responseDAO.save(response);
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * アクションログCSVファイルをデータベースに移行
     * @param csvFilePath CSVファイルパス
     * @param respondentId 回答者ID
     * @return 移行されたログ数
     * @throws IOException ファイル読み込みエラー
     * @throws SQLException データベースエラー
     */
    public int migrateActionLogsFromCSV(String csvFilePath, String respondentId)
            throws IOException, SQLException {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                ActionLogDAO.ActionLog log = parseActionLogLine(line, respondentId);
                if (log != null) {
                    actionLogDAO.save(log);
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * ディレクトリ内のすべての回答CSVファイルを移行
     * @param directoryPath ディレクトリパス
     * @return 移行された回答数
     * @throws IOException ファイル読み込みエラー
     * @throws SQLException データベースエラー
     */
    public int migrateAllResponsesFromDirectory(String directoryPath)
            throws IOException, SQLException {
        int totalCount = 0;
        Path dir = Paths.get(directoryPath);

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return 0;
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            List<Path> csvFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".csv"))
                .toList();

            for (Path csvFile : csvFiles) {
                totalCount += migrateResponsesFromCSV(csvFile.toString());
            }
        }

        return totalCount;
    }

    /**
     * ディレクトリ内のすべてのアクションログCSVファイルを移行
     * @param directoryPath ディレクトリパス
     * @return 移行されたログ数
     * @throws IOException ファイル読み込みエラー
     * @throws SQLException データベースエラー
     */
    public int migrateAllActionLogsFromDirectory(String directoryPath)
            throws IOException, SQLException {
        int totalCount = 0;
        Path dir = Paths.get(directoryPath);

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return 0;
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            List<Path> csvFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".csv"))
                .toList();

            for (Path csvFile : csvFiles) {
                // ファイル名から回答者IDを抽出（例: log_abc123.csv → abc123）
                String fileName = csvFile.getFileName().toString();
                String respondentId = extractRespondentIdFromFileName(fileName);
                totalCount += migrateActionLogsFromCSV(csvFile.toString(), respondentId);
            }
        }

        return totalCount;
    }

    /**
     * CSV行から回答オブジェクトをパース
     */
    private Response parseResponseLine(String line) {
        try {
            String[] parts = parseCsvLine(line);
            if (parts.length < 5) {
                return null;
            }

            String respondentId = parts[0];
            String timestamp = parts[1];
            int questionNum = Integer.parseInt(parts[2]);
            String questionText = parts[3];
            String selectedChoicesStr = parts[4];
            String reason = parts.length > 5 ? parts[5] : "";
            String choiceCombination = parts.length > 6 ? parts[6] : "";

            List<String> selectedChoices = selectedChoicesStr.isEmpty()
                ? new ArrayList<>()
                : Arrays.asList(selectedChoicesStr.split("; "));

            return new Response(
                respondentId,
                timestamp,
                questionNum,
                questionText,
                selectedChoices,
                reason,
                choiceCombination
            );
        } catch (Exception e) {
            System.err.println("Error parsing response line: " + line);
            return null;
        }
    }

    /**
     * CSV行からアクションログをパース
     */
    private ActionLogDAO.ActionLog parseActionLogLine(String line, String respondentId) {
        try {
            String[] parts = parseCsvLine(line);
            if (parts.length < 3) {
                return null;
            }

            String timestamp = parts[0];
            String actionType = parts[1];
            String details = parts.length > 2 ? parts[2] : "";

            return new ActionLogDAO.ActionLog(respondentId, timestamp, actionType, details);
        } catch (Exception e) {
            System.err.println("Error parsing action log line: " + line);
            return null;
        }
    }

    /**
     * CSV行をパース（引用符対応）
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // エスケープされた引用符をスキップ
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    /**
     * ファイル名から回答者IDを抽出
     */
    private String extractRespondentIdFromFileName(String fileName) {
        // log_abc123.csv → abc123
        // response_abc123.csv → abc123
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
        int lastUnderscore = nameWithoutExt.lastIndexOf('_');

        if (lastUnderscore != -1) {
            return nameWithoutExt.substring(lastUnderscore + 1);
        }

        return nameWithoutExt;
    }
}
