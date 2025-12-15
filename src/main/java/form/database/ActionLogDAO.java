package form.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ActionLog エンティティのデータアクセスオブジェクト
 */
public class ActionLogDAO {
    private final DatabaseService dbService;

    public ActionLogDAO(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * Record for representing action log entries
     */
    public record ActionLog(
        long id,
        String respondentId,
        String timestamp,
        String actionType,
        String details
    ) {
        public ActionLog(String respondentId, String timestamp, String actionType, String details) {
            this(0, respondentId, timestamp, actionType, details);
        }
    }

    /**
     * アクションログを保存
     * @param log 保存するログ
     * @return 生成されたID
     * @throws SQLException データベースエラー
     */
    public long save(ActionLog log) throws SQLException {
        String sql = "INSERT INTO action_logs (respondent_id, timestamp, action_type, details) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setString(1, log.respondentId());
            stmt.setString(2, log.timestamp());
            stmt.setString(3, log.actionType());
            stmt.setString(4, log.details());
            stmt.executeUpdate();

            // SQLiteではlast_insert_rowid()を使用
            try (Statement idStmt = dbService.getConnection().createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            throw new SQLException("Failed to get generated action log ID");
        }
    }

    /**
     * 回答者IDですべてのアクションログを取得
     * @param respondentId 回答者ID
     * @return アクションログのリスト
     * @throws SQLException データベースエラー
     */
    public List<ActionLog> findByRespondentId(String respondentId) throws SQLException {
        List<ActionLog> logs = new ArrayList<>();
        String sql = """
            SELECT id, respondent_id, timestamp, action_type, details
            FROM action_logs WHERE respondent_id = ? ORDER BY timestamp
        """;

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setString(1, respondentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(new ActionLog(
                    rs.getLong("id"),
                    rs.getString("respondent_id"),
                    rs.getString("timestamp"),
                    rs.getString("action_type"),
                    rs.getString("details")
                ));
            }
        }

        return logs;
    }

    /**
     * すべてのアクションログを取得
     * @return アクションログのリスト
     * @throws SQLException データベースエラー
     */
    public List<ActionLog> findAll() throws SQLException {
        List<ActionLog> logs = new ArrayList<>();
        String sql = "SELECT id, respondent_id, timestamp, action_type, details FROM action_logs ORDER BY timestamp";

        try (Statement stmt = dbService.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new ActionLog(
                    rs.getLong("id"),
                    rs.getString("respondent_id"),
                    rs.getString("timestamp"),
                    rs.getString("action_type"),
                    rs.getString("details")
                ));
            }
        }

        return logs;
    }

    /**
     * アクションログを削除
     * @param id ログID
     * @throws SQLException データベースエラー
     */
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM action_logs WHERE id = ?";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}
