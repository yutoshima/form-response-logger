package form.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import form.model.Question;
import form.model.Response;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ファイル入出力ユーティリティクラス
 *
 * <p>このクラスは、質問データと回答データのCSV/JSON形式での
 * 読み込みと保存を行うための静的メソッドを提供します。</p>
 *
 * <p>主な機能：</p>
 * <ul>
 *   <li>質問データのCSV/JSON保存・読み込み</li>
 *   <li>回答データのCSV/JSON保存</li>
 *   <li>UTF-8 BOM付きCSV対応</li>
 *   <li>カンマと改行のエスケープ処理</li>
 * </ul>
 *
 * @author Survey App Development Team
 * @version 1.0
 * @since 1.0
 */
public class FileUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final int MAX_CHOICE_COLUMNS = 5;
    private static final char UTF8_BOM = '\ufeff';

    /**
     * 質問データをCSV形式でファイルに保存します。
     *
     * <p>UTF-8 BOM付きで保存され、Excelでも正しく開けます。
     * 選択肢は最大5列まで出力されます。</p>
     *
     * @param questions 保存する質問のリスト
     * @param filepath 保存先ファイルパス
     * @return 保存に成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean saveQuestionsToCSV(List<Question> questions, String filepath) {
        try (PrintWriter writer = createUTF8Writer(filepath)) {
            writer.write(UTF8_BOM);
            writer.println("問題番号,質問文,選択肢1,選択肢2,選択肢3,選択肢4,選択肢5");

            for (int i = 0; i < questions.size(); i++) {
                writer.println(buildQuestionRow(questions.get(i), i + 1));
            }

            return true;
        } catch (IOException e) {
            System.err.println("質問データのCSV保存に失敗しました: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("質問データの処理中にエラーが発生しました: " + e.getMessage());
            return false;
        }
    }

    private static String buildQuestionRow(Question question, int questionNumber) {
        StringBuilder row = new StringBuilder();
        row.append(questionNumber).append(",");
        row.append(escapeCSV(question.getText()));

        for (String choice : question.getChoices()) {
            row.append(",").append(escapeCSV(choice));
        }

        for (int j = question.getChoices().size(); j < MAX_CHOICE_COLUMNS; j++) {
            row.append(",");
        }

        return row.toString();
    }
    
    /**
     * 質問データをJSON形式でファイルに保存します。
     *
     * <p>メタデータ（作成日時、総問題数）も含めて保存されます。</p>
     *
     * @param questions 保存する質問のリスト
     * @param filepath 保存先ファイルパス
     * @return 保存に成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean saveQuestionsToJSON(List<Question> questions, String filepath) {
        return saveAsJSON(filepath, map -> {
            map.put("questions", questions);
            map.put("total_questions", questions.size());
        }, "質問データ");
    }
    
    // 質問データのCSV読み込み
    public static List<Question> loadQuestionsFromCSV(String filepath) {
        List<Question> questions = new ArrayList<>();

        try (BufferedReader reader = createCSVReader(filepath)) {
            skipBOMAndHeader(reader);

            String line;
            while ((line = readCSVRecord(reader)) != null) {
                Question question = parseQuestionFromCSVLine(line);
                if (question != null) {
                    questions.add(question);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    /**
     * CSV読み込み用のBufferedReaderを作成します。
     */
    private static BufferedReader createCSVReader(String filepath) throws IOException {
        return new BufferedReader(
            new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8));
    }

    /**
     * CSVファイルのBOMとヘッダー行をスキップします。
     */
    private static void skipBOMAndHeader(BufferedReader reader) throws IOException {
        // BOMをスキップ
        reader.mark(1);
        if (reader.read() != 0xFEFF) {
            reader.reset();
        }

        // ヘッダーをスキップ
        reader.readLine();
    }

    /**
     * CSV行から質問オブジェクトを生成します。
     */
    private static Question parseQuestionFromCSVLine(String line) {
        String[] parts = parseCSVLine(line);
        if (parts.length < 2) {
            return null;
        }

        Question question = new Question();
        question.setText(parts[1]);
        question.setChoices(extractChoicesFromParts(parts));

        return question;
    }

    /**
     * CSV行のパーツから選択肢リストを抽出します。
     */
    private static List<String> extractChoicesFromParts(String[] parts) {
        List<String> choices = new ArrayList<>();
        for (int i = 2; i < parts.length; i++) {
            if (parts[i] != null && !parts[i].trim().isEmpty()) {
                choices.add(parts[i]);
            }
        }
        return choices;
    }
    
    // 質問データのJSON読み込み
    public static List<Question> loadQuestionsFromJSON(String filepath) {
        List<Question> questions = new ArrayList<>();
        
        try (Reader reader = new InputStreamReader(
                new FileInputStream(filepath), StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(reader, type);
            
            if (data.containsKey("questions")) {
                Type listType = new TypeToken<List<Question>>(){}.getType();
                String json = gson.toJson(data.get("questions"));
                questions = gson.fromJson(json, listType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    // 質問データ読み込み（拡張子で判別）
    public static List<Question> loadQuestions(String filepath) {
        if (filepath.endsWith(".json")) {
            return loadQuestionsFromJSON(filepath);
        } else {
            return loadQuestionsFromCSV(filepath);
        }
    }
    
    // 回答データのCSV保存
    /**
     * 回答データをCSV形式でファイルに保存します。
     *
     * <p>ファイルが既に存在する場合は追記モードで保存されます。
     * 新規ファイルの場合はUTF-8 BOM付きでヘッダー行も出力されます。</p>
     *
     * @param responses 保存する回答のリスト
     * @param filepath 保存先ファイルパス
     * @return 保存に成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean saveResponseToCSV(List<Response> responses, String filepath) {
        return saveToCSVWithAppend(
            filepath,
            "回答者ID,タイムスタンプ,問題番号,質問文,選択した回答,選択組合せ,理由",
            writer -> responses.forEach(response -> writer.println(buildResponseRow(response))),
            "回答データ"
        );
    }

    /**
     * 回答データをJSON形式でファイルに保存します。
     *
     * <p>メタデータ（エクスポート日時、総回答数）も含めて保存されます。</p>
     *
     * @param responses 保存する回答のリスト
     * @param filepath 保存先ファイルパス
     * @return 保存に成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean saveResponseToJSON(List<Response> responses, String filepath) {
        return saveAsJSON(filepath, map -> {
            map.put("responses", responses);
            map.put("total_responses", responses.size());
        }, "回答データ");
    }

    private static boolean saveAsJSON(String filepath, java.util.function.Consumer<Map<String, Object>> dataPopulator, String dataType) {
        try (Writer writer = createUTF8Writer(filepath)) {
            Map<String, Object> data = createMetadataMap();
            dataPopulator.accept(data);
            gson.toJson(data, writer);
            return true;
        } catch (IOException e) {
            System.err.println(dataType + "のJSON保存に失敗しました: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println(dataType + "のシリアライズ中にエラーが発生しました: " + e.getMessage());
            return false;
        }
    }
    
    // ヘルパーメソッド
    private static PrintWriter createUTF8Writer(String filepath) throws IOException {
        return new PrintWriter(
            new OutputStreamWriter(new FileOutputStream(filepath), StandardCharsets.UTF_8));
    }
    
    private static Map<String, Object> createMetadataMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("export_date", LocalDateTime.now().format(TIMESTAMP_FORMAT));
        return map;
    }
    
    private static String buildResponseRow(Response response) {
        StringBuilder row = new StringBuilder();
        row.append(escapeCSV(response.getRespondentId())).append(",");
        row.append(escapeCSV(response.getTimestamp())).append(",");
        row.append(response.getQuestionNum()).append(",");
        row.append(escapeCSV(response.getQuestionText())).append(",");
        row.append(escapeCSV(response.getSelectedChoice())).append(",");
        row.append(escapeCSV(response.getChoiceCombination() != null ? response.getChoiceCombination() : "")).append(",");
        row.append(escapeCSV(response.getReason()));
        return row.toString();
    }
    
    // 回答データ保存（形式指定）
    public static boolean saveResponse(List<Response> responses, String filepath, String outputFormat) {
        boolean success = true;

        if (shouldSaveFormat(outputFormat, "csv")) {
            success &= saveResponseToCSV(responses, ensureCSVExtension(filepath));
        }

        if (shouldSaveFormat(outputFormat, "json")) {
            success &= saveResponseToJSON(responses, convertToJSONPath(filepath));
        }

        return success;
    }

    /**
     * 指定された形式で保存すべきかを判定します。
     */
    private static boolean shouldSaveFormat(String outputFormat, String targetFormat) {
        return targetFormat.equals(outputFormat) || "both".equals(outputFormat);
    }

    /**
     * ファイルパスにCSV拡張子を付与します。
     */
    private static String ensureCSVExtension(String filepath) {
        return filepath.endsWith(".csv") ? filepath : filepath + ".csv";
    }

    /**
     * ファイルパスをJSON形式に変換します。
     */
    private static String convertToJSONPath(String filepath) {
        String jsonPath = filepath.replace(".csv", ".json");
        if (!jsonPath.endsWith(".json")) {
            jsonPath += ".json";
        }
        return jsonPath;
    }
    
    // CSV文字列のエスケープ
    private static String escapeCSV(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    /**
     * CSV形式でデータを追記保存します。
     * ファイルが存在しない場合はBOMとヘッダーを書き込みます。
     *
     * @param filepath 保存先ファイルパス
     * @param header ヘッダー行
     * @param dataWriter データ行を書き込むConsumer
     * @param dataType データタイプ（エラーメッセージ用）
     * @return 保存に成功した場合はtrue、失敗した場合はfalse
     */
    private static boolean saveToCSVWithAppend(
            String filepath,
            String header,
            java.util.function.Consumer<PrintWriter> dataWriter,
            String dataType) {
        File file = new File(filepath);
        boolean fileExists = file.exists();

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filepath, true), StandardCharsets.UTF_8))) {

            if (!fileExists) {
                writeCSVHeader(writer, header);
            }

            dataWriter.accept(writer);

            return true;
        } catch (IOException e) {
            System.err.println(dataType + "のCSV保存に失敗しました: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println(dataType + "の処理中にエラーが発生しました: " + e.getMessage());
            return false;
        }
    }

    /**
     * CSVファイルのヘッダーをBOM付きで書き込みます。
     */
    private static void writeCSVHeader(PrintWriter writer, String header) {
        writer.write(UTF8_BOM);
        writer.println(header);
    }

    // 複数行にわたるCSVレコードを読み取る
    private static String readCSVRecord(BufferedReader reader) throws IOException {
        StringBuilder record = new StringBuilder();
        String line;
        boolean inQuotes = false;

        while ((line = reader.readLine()) != null) {
            appendLineToRecord(record, line);
            inQuotes = updateQuoteState(line, inQuotes);

            // クォートが閉じていればレコード完了
            if (!inQuotes) {
                break;
            }
        }

        return record.length() > 0 ? record.toString() : null;
    }

    /**
     * レコードに行を追加します。
     */
    private static void appendLineToRecord(StringBuilder record, String line) {
        if (record.length() > 0) {
            record.append("\n");
        }
        record.append(line);
    }

    /**
     * 行内のクォート状態を更新します。
     */
    private static boolean updateQuoteState(String line, boolean currentState) {
        boolean inQuotes = currentState;

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                // エスケープされたダブルクォート（""）をスキップ
                if (isEscapedQuote(line, i)) {
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            }
        }

        return inQuotes;
    }

    /**
     * 指定位置のクォートがエスケープされているかを判定します。
     */
    private static boolean isEscapedQuote(String line, int position) {
        return position + 1 < line.length() && line.charAt(position + 1) == '"';
    }

    // CSV行のパース
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            CSVParseResult parseResult = processCSVCharacter(c, i, line, inQuotes, current);

            inQuotes = parseResult.inQuotes;
            i = parseResult.nextIndex;

            if (parseResult.shouldAddField) {
                result.add(current.toString());
                current.setLength(0);
            }
        }

        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    /**
     * CSV文字処理の結果を保持するクラス
     */
    private static class CSVParseResult {
        boolean inQuotes;
        int nextIndex;
        boolean shouldAddField;

        CSVParseResult(boolean inQuotes, int nextIndex, boolean shouldAddField) {
            this.inQuotes = inQuotes;
            this.nextIndex = nextIndex;
            this.shouldAddField = shouldAddField;
        }
    }

    /**
     * CSV行の1文字を処理します。
     */
    private static CSVParseResult processCSVCharacter(
            char c, int index, String line, boolean inQuotes, StringBuilder current) {

        if (c == '"') {
            return processQuoteCharacter(index, line, inQuotes, current);
        } else if (c == ',' && !inQuotes) {
            return new CSVParseResult(inQuotes, index, true);
        } else {
            current.append(c);
            return new CSVParseResult(inQuotes, index, false);
        }
    }

    /**
     * クォート文字を処理します。
     */
    private static CSVParseResult processQuoteCharacter(
            int index, String line, boolean inQuotes, StringBuilder current) {

        if (inQuotes && isEscapedQuote(line, index)) {
            current.append('"');
            return new CSVParseResult(true, index + 1, false);
        } else {
            return new CSVParseResult(!inQuotes, index, false);
        }
    }
    
    public static String getTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }

    /**
     * 組み合わせパターンをCSV形式でファイルに保存します。
     *
     * @param responses 保存する回答のリスト
     * @param filepath 保存先ファイルパス
     * @return 保存に成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean saveCombinationPatterns(List<Response> responses, String filepath) {
        return saveToCSVWithAppend(
            filepath,
            "回答者ID,タイムスタンプ,問題番号,選択組合せ",
            writer -> responses.forEach(response -> writer.println(buildCombinationRow(response))),
            "組み合わせパターン"
        );
    }

    /**
     * 組み合わせパターンの行データを構築します。
     */
    private static String buildCombinationRow(Response response) {
        StringBuilder row = new StringBuilder();
        row.append(escapeCSV(response.getRespondentId())).append(",");
        row.append(escapeCSV(response.getTimestamp())).append(",");
        row.append(response.getQuestionNum()).append(",");
        row.append(escapeCSV(response.getChoiceCombination() != null ? response.getChoiceCombination() : ""));
        return row.toString();
    }
}
