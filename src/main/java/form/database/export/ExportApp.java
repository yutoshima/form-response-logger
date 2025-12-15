package form.database.export;

import form.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * データベースからデータをエクスポートするコマンドラインツール
 */
public class ExportApp {
    private static final Logger logger = LoggerFactory.getLogger(ExportApp.class);

    public static void main(String[] args) {
        System.out.println("=== Database Export Tool ===\n");

        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }

        String dbPath = args[0];
        String format = args[1];  // csv, json, excel
        String outputPath = args[2];

        logger.info("Database path: {}", dbPath);
        logger.info("Export format: {}", format);
        logger.info("Output path: {}", outputPath);

        DatabaseService dbService = new DatabaseService(dbPath);
        DataExporter exporter = new DataExporter(dbService);

        try {
            switch (format.toLowerCase()) {
                case "csv" -> exportToCSV(exporter, outputPath);
                case "json" -> exportToJSON(exporter, outputPath);
                case "excel" -> exportToExcel(exporter, outputPath);
                default -> {
                    System.err.println("Unsupported format: " + format);
                    printUsage();
                    System.exit(1);
                }
            }

            System.out.println("\n=== Export completed successfully! ===");
        } catch (Exception e) {
            logger.error("Export failed", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } finally {
            dbService.close();
        }
    }

    private static void exportToCSV(DataExporter exporter, String basePath) {
        System.out.println("Exporting to CSV format...");
        exporter.exportQuestionsToCSV(basePath + "_questions.csv");
        exporter.exportResponsesToCSV(basePath + "_responses.csv");
        exporter.exportActionLogsToCSV(basePath + "_logs.csv");
        System.out.println("✓ Exported to:");
        System.out.println("  - " + basePath + "_questions.csv");
        System.out.println("  - " + basePath + "_responses.csv");
        System.out.println("  - " + basePath + "_logs.csv");
    }

    private static void exportToJSON(DataExporter exporter, String basePath) {
        System.out.println("Exporting to JSON format...");
        exporter.exportQuestionsToJSON(basePath + "_questions.json");
        exporter.exportResponsesToJSON(basePath + "_responses.json");
        System.out.println("✓ Exported to:");
        System.out.println("  - " + basePath + "_questions.json");
        System.out.println("  - " + basePath + "_responses.json");
    }

    private static void exportToExcel(DataExporter exporter, String basePath) {
        System.out.println("Exporting to Excel format...");
        exporter.exportResponsesToExcel(basePath + "_responses.xlsx");
        System.out.println("✓ Exported to:");
        System.out.println("  - " + basePath + "_responses.xlsx");
    }

    private static void printUsage() {
        System.out.println("Usage: java -cp target/form-app-1.0.0.jar form.database.export.ExportApp <db_path> <format> <output_path>");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  db_path      Path to the SQLite database file");
        System.out.println("  format       Export format (csv, json, excel)");
        System.out.println("  output_path  Base path for output files");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -cp target/form-app-1.0.0.jar form.database.export.ExportApp data/survey.db csv export/data");
        System.out.println("  java -cp target/form-app-1.0.0.jar form.database.export.ExportApp data/survey.db json export/data");
        System.out.println("  java -cp target/form-app-1.0.0.jar form.database.export.ExportApp data/survey.db excel export/data");
    }
}
