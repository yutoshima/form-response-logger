package com.study.form.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.study.form.model.Question;
import com.study.form.model.Response;

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
                Question q = questions.get(i);
                StringBuilder row = new StringBuilder();
                row.append(i + 1).append(",");
                row.append(escapeCSV(q.getText()));

                for (String choice : q.getChoices()) {
                    row.append(",").append(escapeCSV(choice));
                }

                // 最大列数に満たない場合は空文字で埋める
                for (int j = q.getChoices().size(); j < MAX_CHOICE_COLUMNS; j++) {
                    row.append(",");
                }

                writer.println(row.toString());
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
        try (Writer writer = createUTF8Writer(filepath)) {
            Map<String, Object> data = createMetadataMap();
            data.put("questions", questions);
            data.put("total_questions", questions.size());

            gson.toJson(data, writer);
            return true;
        } catch (IOException e) {
            System.err.println("質問データのJSON保存に失敗しました: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("質問データのシリアライズ中にエラーが発生しました: " + e.getMessage());
            return false;
        }
    }
    
    // 質問データのCSV読み込み
    public static List<Question> loadQuestionsFromCSV(String filepath) {
        List<Question> questions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8))) {
            // BOMをスキップ
            reader.mark(1);
            if (reader.read() != 0xFEFF) {
                reader.reset();
            }
            
            // ヘッダーをスキップ
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length < 2) continue;
                
                Question question = new Question();
                question.setText(parts[1]);
                
                List<String> choices = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    if (parts[i] != null && !parts[i].trim().isEmpty()) {
                        choices.add(parts[i]);
                    }
                }
                question.setChoices(choices);
                questions.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return questions;
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
        File file = new File(filepath);
        boolean fileExists = file.exists();

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filepath, true), StandardCharsets.UTF_8))) {

            if (!fileExists) {
                writer.write(UTF8_BOM);
                writer.println("回答者ID,タイムスタンプ,問題番号,質問文,選択した回答,理由");
            }

            for (Response response : responses) {
                writer.println(buildResponseRow(response));
            }

            return true;
        } catch (IOException e) {
            System.err.println("回答データのCSV保存に失敗しました: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("回答データの処理中にエラーが発生しました: " + e.getMessage());
            return false;
        }
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
        try (Writer writer = createUTF8Writer(filepath)) {
            Map<String, Object> data = createMetadataMap();
            data.put("responses", responses);
            data.put("total_responses", responses.size());

            gson.toJson(data, writer);
            return true;
        } catch (IOException e) {
            System.err.println("回答データのJSON保存に失敗しました: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("回答データのシリアライズ中にエラーが発生しました: " + e.getMessage());
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
        row.append(escapeCSV(response.getReason()));
        return row.toString();
    }
    
    // 回答データ保存（形式指定）
    public static boolean saveResponse(List<Response> responses, String filepath, String outputFormat) {
        boolean success = true;
        
        if ("csv".equals(outputFormat) || "both".equals(outputFormat)) {
            String csvPath = filepath.endsWith(".csv") ? filepath : filepath + ".csv";
            if (!saveResponseToCSV(responses, csvPath)) {
                success = false;
            }
        }
        
        if ("json".equals(outputFormat) || "both".equals(outputFormat)) {
            String jsonPath = filepath.replace(".csv", ".json");
            if (!jsonPath.endsWith(".json")) {
                jsonPath += ".json";
            }
            if (!saveResponseToJSON(responses, jsonPath)) {
                success = false;
            }
        }
        
        return success;
    }
    
    // CSV文字列のエスケープ
    private static String escapeCSV(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
    
    // CSV行のパース
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
    
    public static String getTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
}
