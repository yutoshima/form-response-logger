package form.database.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import form.database.*;
import form.exception.ExportException;
import form.model.Question;
import form.model.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * データベースからデータをエクスポートするクラス
 */
public class DataExporter {
    private static final Logger logger = LoggerFactory.getLogger(DataExporter.class);

    private final DatabaseService dbService;
    private final QuestionDAO questionDAO;
    private final ResponseDAO responseDAO;
    private final ActionLogDAO actionLogDAO;

    public DataExporter(DatabaseService dbService) {
        this.dbService = dbService;
        this.questionDAO = new QuestionDAO(dbService);
        this.responseDAO = new ResponseDAO(dbService);
        this.actionLogDAO = new ActionLogDAO(dbService);
    }

    /**
     * 質問をCSVにエクスポート
     */
    public void exportQuestionsToCSV(String filePath) {
        logger.info("Exporting questions to CSV: {}", filePath);
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Question> questions = questionDAO.findAll();

            // ヘッダー
            writer.write("Question,Choices\n");

            for (Question q : questions) {
                writer.write(escapeCSV(q.text()) + ",");
                writer.write(escapeCSV(String.join("; ", q.choices())) + "\n");
            }

            logger.info("Successfully exported {} questions to CSV", questions.size());
        } catch (SQLException | IOException e) {
            logger.error("Failed to export questions to CSV", e);
            throw new ExportException("Failed to export questions to CSV: " + e.getMessage(), e);
        }
    }

    /**
     * 回答をCSVにエクスポート
     */
    public void exportResponsesToCSV(String filePath) {
        logger.info("Exporting responses to CSV: {}", filePath);
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Response> responses = responseDAO.findAll();

            // ヘッダー
            writer.write("Respondent ID,Timestamp,Question Num,Question Text,Selected Choices,Reason,Choice Combination\n");

            for (Response r : responses) {
                writer.write(escapeCSV(r.respondentId()) + ",");
                writer.write(escapeCSV(r.timestamp()) + ",");
                writer.write(r.questionNum() + ",");
                writer.write(escapeCSV(r.questionText()) + ",");
                writer.write(escapeCSV(String.join("; ", r.selectedChoices())) + ",");
                writer.write(escapeCSV(r.reason()) + ",");
                writer.write(escapeCSV(r.choiceCombination()) + "\n");
            }

            logger.info("Successfully exported {} responses to CSV", responses.size());
        } catch (SQLException | IOException e) {
            logger.error("Failed to export responses to CSV", e);
            throw new ExportException("Failed to export responses to CSV: " + e.getMessage(), e);
        }
    }

    /**
     * アクションログをCSVにエクスポート
     */
    public void exportActionLogsToCSV(String filePath) {
        logger.info("Exporting action logs to CSV: {}", filePath);
        try (FileWriter writer = new FileWriter(filePath)) {
            List<ActionLogDAO.ActionLog> logs = actionLogDAO.findAll();

            // ヘッダー
            writer.write("Respondent ID,Timestamp,Action Type,Details\n");

            for (ActionLogDAO.ActionLog log : logs) {
                writer.write(escapeCSV(log.respondentId()) + ",");
                writer.write(escapeCSV(log.timestamp()) + ",");
                writer.write(escapeCSV(log.actionType()) + ",");
                writer.write(escapeCSV(log.details()) + "\n");
            }

            logger.info("Successfully exported {} action logs to CSV", logs.size());
        } catch (SQLException | IOException e) {
            logger.error("Failed to export action logs to CSV", e);
            throw new ExportException("Failed to export action logs to CSV: " + e.getMessage(), e);
        }
    }

    /**
     * 質問をJSONにエクスポート
     */
    public void exportQuestionsToJSON(String filePath) {
        logger.info("Exporting questions to JSON: {}", filePath);
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Question> questions = questionDAO.findAll();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(questions, writer);
            logger.info("Successfully exported {} questions to JSON", questions.size());
        } catch (SQLException | IOException e) {
            logger.error("Failed to export questions to JSON", e);
            throw new ExportException("Failed to export questions to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * 回答をJSONにエクスポート
     */
    public void exportResponsesToJSON(String filePath) {
        logger.info("Exporting responses to JSON: {}", filePath);
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Response> responses = responseDAO.findAll();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(responses, writer);
            logger.info("Successfully exported {} responses to JSON", responses.size());
        } catch (SQLException | IOException e) {
            logger.error("Failed to export responses to JSON", e);
            throw new ExportException("Failed to export responses to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * 回答をExcelにエクスポート
     */
    public void exportResponsesToExcel(String filePath) {
        logger.info("Exporting responses to Excel: {}", filePath);
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(filePath)) {

            List<Response> responses = responseDAO.findAll();
            Sheet sheet = workbook.createSheet("Responses");

            // ヘッダー行を作成
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Respondent ID", "Timestamp", "Question Num", "Question Text",
                               "Selected Choices", "Reason", "Choice Combination"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);

                // ヘッダーのスタイル
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                cell.setCellStyle(headerStyle);
            }

            // データ行を作成
            int rowNum = 1;
            for (Response r : responses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.respondentId());
                row.createCell(1).setCellValue(r.timestamp());
                row.createCell(2).setCellValue(r.questionNum());
                row.createCell(3).setCellValue(r.questionText());
                row.createCell(4).setCellValue(String.join("; ", r.selectedChoices()));
                row.createCell(5).setCellValue(r.reason());
                row.createCell(6).setCellValue(r.choiceCombination());
            }

            // 列幅を自動調整
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            logger.info("Successfully exported {} responses to Excel", responses.size());
        } catch (SQLException | IOException e) {
            logger.error("Failed to export responses to Excel", e);
            throw new ExportException("Failed to export responses to Excel: " + e.getMessage(), e);
        }
    }

    /**
     * CSVエスケープ処理
     */
    private String escapeCSV(String text) {
        if (text == null) {
            return "";
        }
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
