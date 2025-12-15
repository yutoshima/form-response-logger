package form.database;

import form.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * データベーストランザクションを管理するクラス
 */
public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    private final DatabaseService dbService;

    public TransactionManager(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * トランザクション内で処理を実行
     * @param operation 実行する処理
     * @param <T> 戻り値の型
     * @return 処理の結果
     */
    public <T> T executeInTransaction(Supplier<T> operation) {
        Connection conn = null;
        boolean originalAutoCommit = true;

        try {
            conn = dbService.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            logger.debug("Transaction started");

            T result = operation.get();

            conn.commit();
            logger.debug("Transaction committed successfully");

            return result;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Transaction rolled back due to error", e);
                } catch (SQLException rollbackEx) {
                    logger.error("Failed to rollback transaction", rollbackEx);
                }
            }
            throw new DatabaseException("Transaction failed: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    logger.error("Failed to restore autoCommit setting", e);
                }
            }
        }
    }

    /**
     * トランザクション内で処理を実行（戻り値なし）
     * @param operation 実行する処理
     */
    public void executeInTransaction(Runnable operation) {
        executeInTransaction(() -> {
            operation.run();
            return null;
        });
    }

    /**
     * バッチ操作用のトランザクション実行
     * @param batchSize バッチサイズ
     * @param operation 実行する処理
     * @param <T> 戻り値の型
     * @return 処理の結果
     */
    public <T> T executeInBatch(int batchSize, Supplier<T> operation) {
        logger.debug("Starting batch operation with batch size: {}", batchSize);
        return executeInTransaction(operation);
    }
}
