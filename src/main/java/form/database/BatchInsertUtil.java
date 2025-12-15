package form.database;

import form.exception.DatabaseException;
import form.model.Question;
import form.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * バッチINSERT処理を提供するユーティリティクラス
 */
public class BatchInsertUtil {
    private static final Logger logger = LoggerFactory.getLogger(BatchInsertUtil.class);
    private static final int DEFAULT_BATCH_SIZE = 100;

    private final DatabaseService dbService;

    public BatchInsertUtil(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * 質問をバッチINSERT
     * @param questions 質問のリスト
     * @return 挿入された件数
     */
    public int batchInsertQuestions(List<Question> questions) {
        return batchInsertQuestions(questions, DEFAULT_BATCH_SIZE);
    }

    /**
     * 質問をバッチINSERT（バッチサイズ指定）
     * @param questions 質問のリスト
     * @param batchSize バッチサイズ
     * @return 挿入された件数
     */
    public int batchInsertQuestions(List<Question> questions, int batchSize) {
        logger.info("Starting batch insert of {} questions with batch size {}", questions.size(), batchSize);

        String sql = "INSERT INTO questions (text) VALUES (?)";
        int count = 0;

        try {
            Connection conn = dbService.getConnection();
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int batchCount = 0;

                for (Question question : questions) {
                    stmt.setString(1, question.text());
                    stmt.addBatch();
                    batchCount++;

                    if (batchCount % batchSize == 0) {
                        int[] results = stmt.executeBatch();
                        count += results.length;
                        logger.debug("Executed batch of {} questions", results.length);
                    }
                }

                // 残りのバッチを実行
                if (batchCount % batchSize != 0) {
                    int[] results = stmt.executeBatch();
                    count += results.length;
                    logger.debug("Executed final batch of {} questions", results.length);
                }

                conn.commit();
                logger.info("Successfully inserted {} questions in batches", count);

            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }

        } catch (SQLException e) {
            logger.error("Batch insert of questions failed", e);
            throw new DatabaseException("Failed to batch insert questions: " + e.getMessage(), e);
        }

        return count;
    }

    /**
     * 回答をバッチINSERT
     * @param responses 回答のリスト
     * @return 挿入された件数
     */
    public int batchInsertResponses(List<Response> responses) {
        return batchInsertResponses(responses, DEFAULT_BATCH_SIZE);
    }

    /**
     * 回答をバッチINSERT（バッチサイズ指定）
     * @param responses 回答のリスト
     * @param batchSize バッチサイズ
     * @return 挿入された件数
     */
    public int batchInsertResponses(List<Response> responses, int batchSize) {
        logger.info("Starting batch insert of {} responses with batch size {}", responses.size(), batchSize);

        String sql = """
            INSERT INTO responses (respondent_id, timestamp, question_num, question_text, reason, choice_combination)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        int count = 0;

        try {
            Connection conn = dbService.getConnection();
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int batchCount = 0;

                for (Response response : responses) {
                    stmt.setString(1, response.respondentId());
                    stmt.setString(2, response.timestamp());
                    stmt.setInt(3, response.questionNum());
                    stmt.setString(4, response.questionText());
                    stmt.setString(5, response.reason());
                    stmt.setString(6, response.choiceCombination());
                    stmt.addBatch();
                    batchCount++;

                    if (batchCount % batchSize == 0) {
                        int[] results = stmt.executeBatch();
                        count += results.length;
                        logger.debug("Executed batch of {} responses", results.length);
                    }
                }

                // 残りのバッチを実行
                if (batchCount % batchSize != 0) {
                    int[] results = stmt.executeBatch();
                    count += results.length;
                    logger.debug("Executed final batch of {} responses", results.length);
                }

                conn.commit();
                logger.info("Successfully inserted {} responses in batches", count);

            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }

        } catch (SQLException e) {
            logger.error("Batch insert of responses failed", e);
            throw new DatabaseException("Failed to batch insert responses: " + e.getMessage(), e);
        }

        return count;
    }
}
