package form.database;

import form.model.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Response エンティティのデータアクセスオブジェクト
 */
public class ResponseDAO {
    private final DatabaseService dbService;

    public ResponseDAO(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * 回答を保存
     * @param response 保存する回答
     * @return 生成されたID
     * @throws SQLException データベースエラー
     */
    public long save(Response response) throws SQLException {
        String sql = """
            INSERT INTO responses (respondent_id, timestamp, question_num, question_text, reason, choice_combination)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setString(1, response.respondentId());
            stmt.setString(2, response.timestamp());
            stmt.setInt(3, response.questionNum());
            stmt.setString(4, response.questionText());
            stmt.setString(5, response.reason());
            stmt.setString(6, response.choiceCombination());
            stmt.executeUpdate();

            // SQLiteではlast_insert_rowid()を使用
            try (Statement idStmt = dbService.getConnection().createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    long responseId = rs.getLong(1);
                    saveSelectedChoices(responseId, response.selectedChoices());
                    return responseId;
                }
            }
            throw new SQLException("Failed to get generated response ID");
        }
    }

    /**
     * 選択された選択肢を保存
     */
    private void saveSelectedChoices(long responseId, List<String> selectedChoices) throws SQLException {
        String sql = "INSERT INTO selected_choices (response_id, choice_text) VALUES (?, ?)";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            for (String choice : selectedChoices) {
                stmt.setLong(1, responseId);
                stmt.setString(2, choice);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * 回答者IDですべての回答を取得
     * @param respondentId 回答者ID
     * @return 回答のリスト
     * @throws SQLException データベースエラー
     */
    public List<Response> findByRespondentId(String respondentId) throws SQLException {
        List<Response> responses = new ArrayList<>();
        String sql = """
            SELECT id, respondent_id, timestamp, question_num, question_text, reason, choice_combination
            FROM responses WHERE respondent_id = ? ORDER BY question_num
        """;

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setString(1, respondentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id");
                String timestamp = rs.getString("timestamp");
                int questionNum = rs.getInt("question_num");
                String questionText = rs.getString("question_text");
                String reason = rs.getString("reason");
                String choiceCombination = rs.getString("choice_combination");

                List<String> selectedChoices = findSelectedChoicesByResponseId(id);

                responses.add(new Response(
                    respondentId,
                    timestamp,
                    questionNum,
                    questionText,
                    selectedChoices,
                    reason,
                    choiceCombination
                ));
            }
        }

        return responses;
    }

    /**
     * すべての回答を取得
     * @return 回答のリスト
     * @throws SQLException データベースエラー
     */
    public List<Response> findAll() throws SQLException {
        List<Response> responses = new ArrayList<>();
        String sql = """
            SELECT id, respondent_id, timestamp, question_num, question_text, reason, choice_combination
            FROM responses ORDER BY respondent_id, question_num
        """;

        try (Statement stmt = dbService.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String respondentId = rs.getString("respondent_id");
                String timestamp = rs.getString("timestamp");
                int questionNum = rs.getInt("question_num");
                String questionText = rs.getString("question_text");
                String reason = rs.getString("reason");
                String choiceCombination = rs.getString("choice_combination");

                List<String> selectedChoices = findSelectedChoicesByResponseId(id);

                responses.add(new Response(
                    respondentId,
                    timestamp,
                    questionNum,
                    questionText,
                    selectedChoices,
                    reason,
                    choiceCombination
                ));
            }
        }

        return responses;
    }

    /**
     * 回答IDで選択された選択肢を取得
     */
    private List<String> findSelectedChoicesByResponseId(long responseId) throws SQLException {
        List<String> choices = new ArrayList<>();
        String sql = "SELECT choice_text FROM selected_choices WHERE response_id = ?";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, responseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                choices.add(rs.getString("choice_text"));
            }
        }

        return choices;
    }

    /**
     * 回答を削除
     * @param id 回答ID
     * @throws SQLException データベースエラー
     */
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM responses WHERE id = ?";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}
