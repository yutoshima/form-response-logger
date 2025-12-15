package form.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 回答データモデル（Java 17 Record）
 */
public record Response(
    String respondentId,
    String timestamp,
    int questionNum,
    String questionText,
    List<String> selectedChoices,
    String reason,
    String choiceCombination
) {

    /**
     * コンパクトコンストラクタ（バリデーションと防御的コピー）
     */
    public Response {
        // 防御的コピーで不変性を保証
        selectedChoices = selectedChoices != null ? List.copyOf(selectedChoices) : List.of();
        choiceCombination = choiceCombination != null ? choiceCombination : "";
    }

    /**
     * デフォルトコンストラクタ（JSON/CSV デシリアライゼーション用）
     */
    public Response() {
        this("", "", 0, "", new ArrayList<>(), "", "");
    }

    /**
     * 6パラメータコンストラクタ（後方互換性のため）
     */
    public Response(String respondentId, String timestamp, int questionNum,
                    String questionText, List<String> selectedChoices, String reason) {
        this(respondentId, timestamp, questionNum, questionText, selectedChoices, reason, "");
    }

    /**
     * 後方互換性のためのgetterメソッド
     */
    public String getRespondentId() {
        return respondentId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getSelectedChoices() {
        return selectedChoices;
    }

    public String getReason() {
        return reason;
    }

    public String getChoiceCombination() {
        return choiceCombination;
    }

    /**
     * 選択した選択肢を結合した文字列を取得します。
     */
    public String getSelectedChoice() {
        if (selectedChoices == null || selectedChoices.isEmpty()) {
            return "";
        }
        return String.join("; ", selectedChoices);
    }
}
