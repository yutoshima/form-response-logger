package form.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 質問データモデル（Java 17 Record）
 */
public record Question(String text, List<String> choices) {

    /**
     * コンパクトコンストラクタ（バリデーションと防御的コピー）
     */
    public Question {
        // 防御的コピーで不変性を保証
        choices = choices != null ? List.copyOf(choices) : List.of();
    }

    /**
     * デフォルトコンストラクタ（JSON/CSV デシリアライゼーション用）
     */
    public Question() {
        this("", new ArrayList<>());
    }

    /**
     * 後方互換性のためのgetterメソッド
     */
    public String getText() {
        return text;
    }

    public List<String> getChoices() {
        return choices;
    }

    /**
     * 後方互換性のためのsetterメソッド（新しいインスタンスを返す）
     */
    public Question setText(String newText) {
        return new Question(newText, choices);
    }

    public Question setChoices(List<String> newChoices) {
        return new Question(text, newChoices);
    }
}
