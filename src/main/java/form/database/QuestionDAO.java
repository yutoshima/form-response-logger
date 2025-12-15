package form.database;

import form.exception.DatabaseException;
import form.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Question エンティティのデータアクセスオブジェクト
 */
public class QuestionDAO {
    private static final Logger logger = LoggerFactory.getLogger(QuestionDAO.class);
    private final DatabaseService dbService;

    public QuestionDAO(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * 質問を保存（選択肢も含む）
     * @param question 保存する質問
     * @return 生成されたID
     * @throws SQLException データベースエラー
     */
    public long save(Question question) throws SQLException {
        String sql = "INSERT INTO questions (text) VALUES (?)";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setString(1, question.text());
            stmt.executeUpdate();

            // SQLiteではlast_insert_rowid()を使用
            try (Statement idStmt = dbService.getConnection().createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    long questionId = rs.getLong(1);
                    saveChoices(questionId, question.choices());
                    return questionId;
                }
            }
            throw new SQLException("Failed to get generated question ID");
        }
    }

    /**
     * 質問の選択肢を保存
     */
    private void saveChoices(long questionId, List<String> choices) throws SQLException {
        String sql = "INSERT INTO choices (question_id, choice_text, choice_order) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < choices.size(); i++) {
                stmt.setLong(1, questionId);
                stmt.setString(2, choices.get(i));
                stmt.setInt(3, i);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * すべての質問を取得
     * @return 質問のリスト
     * @throws SQLException データベースエラー
     */
    public List<Question> findAll() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT id, text FROM questions ORDER BY id";

        try (Statement stmt = dbService.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String text = rs.getString("text");
                List<String> choices = findChoicesByQuestionId(id);
                questions.add(new Question(text, choices));
            }
        }

        return questions;
    }

    /**
     * IDで質問を取得
     * @param id 質問ID
     * @return 質問（見つからない場合はnull）
     * @throws SQLException データベースエラー
     */
    public Question findById(long id) throws SQLException {
        String sql = "SELECT text FROM questions WHERE id = ?";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String text = rs.getString("text");
                List<String> choices = findChoicesByQuestionId(id);
                return new Question(text, choices);
            }
        }

        return null;
    }

    /**
     * 質問IDで選択肢を取得
     */
    private List<String> findChoicesByQuestionId(long questionId) throws SQLException {
        List<String> choices = new ArrayList<>();
        String sql = "SELECT choice_text FROM choices WHERE question_id = ? ORDER BY choice_order";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, questionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                choices.add(rs.getString("choice_text"));
            }
        }

        return choices;
    }

    /**
     * 質問を削除
     * @param id 質問ID
     * @throws SQLException データベースエラー
     */
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * すべての質問を削除
     * @throws SQLException データベースエラー
     */
    public void deleteAll() throws SQLException {
        try (Statement stmt = dbService.getConnection().createStatement()) {
            stmt.execute("DELETE FROM questions");
        }
    }
}
