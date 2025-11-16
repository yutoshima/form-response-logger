# 開発ガイドライン

本ドキュメントは、研究用アンケートシステム（Java版）の開発・保守を行う際のガイドラインです。

## 目次

1. [開発環境のセットアップ](#開発環境のセットアップ)
2. [コーディング規約](#コーディング規約)
3. [プロジェクト構造](#プロジェクト構造)
4. [新機能の追加方法](#新機能の追加方法)
5. [テストとビルド](#テストとビルド)
6. [コミットガイドライン](#コミットガイドライン)

## 開発環境のセットアップ

### 必要な環境

- **Java**: OpenJDK 11以上
- **Maven**: 3.6以上
- **IDE**: IntelliJ IDEA、Eclipse、VS Code等（任意）

### 初回セットアップ

```bash
# リポジトリのクローン
git clone <repository-url>
cd study_form_app_java

# 依存関係のダウンロードとビルド
mvn clean install

# アプリケーションの起動
mvn exec:java -Dexec.mainClass="com.study.form.SurveyApp"
```

### IDEの設定

#### IntelliJ IDEA
1. `File` → `Open` でプロジェクトディレクトリを開く
2. Mavenプロジェクトとして自動認識されます
3. `Run` → `Edit Configurations` で実行構成を作成
   - Main class: `com.study.form.SurveyApp`

#### Eclipse
1. `File` → `Import` → `Maven` → `Existing Maven Projects`
2. プロジェクトディレクトリを選択

## コーディング規約

### 基本原則

1. **可読性優先**: コードは書くよりも読まれることが多いため、可読性を重視
2. **DRY原則**: 同じコードを繰り返さない（Don't Repeat Yourself）
3. **単一責任の原則**: 1つのクラス/メソッドは1つの責任のみを持つ
4. **定数の使用**: マジックナンバーや文字列は`Constants.java`で定義

### Javaコーディングスタイル

#### 命名規則

```java
// クラス名: PascalCase
public class ConfigManager { }

// メソッド名: camelCase
public void loadConfig() { }

// 変数名: camelCase
private String questionsFile;

// 定数: UPPER_SNAKE_CASE
public static final String CONFIG_FILE = "config.json";

// パッケージ名: 小文字のみ
package com.study.form.util;
```

#### インデントとフォーマット

- インデント: スペース4つ
- 1行の長さ: 100文字を推奨（最大120文字）
- 中括弧: K&Rスタイル（開き括弧は同じ行）

```java
// 良い例
public void saveConfig() {
    if (config != null) {
        // 処理
    }
}

// 悪い例
public void saveConfig()
{
    if(config!=null){
        // 処理
    }
}
```

#### Javadocの記述

全てのpublicクラスとメソッドにJavadocを記述してください。

```java
/**
 * 設定ファイルを読み込みます。
 *
 * <p>設定ファイルが存在しない場合は、デフォルト設定を生成して保存します。</p>
 *
 * @throws IOException ファイル読み込みに失敗した場合
 */
public void loadConfig() throws IOException {
    // 実装
}
```

### UIコンポーネントの規約

1. **レイアウト**: 適切なLayoutManagerを使用
   - GridLayout: 均等配置が必要な場合（選択肢ボタンなど）
   - BoxLayout: 垂直/水平の単純な配置
   - BorderLayout: 5つの領域への配置

2. **色とフォント**: `Constants.java`で定義された定数を使用

```java
// 良い例
button.setBackground(Constants.COLOR_SELECTED);
label.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_LABEL));

// 悪い例
button.setBackground(new Color(76, 175, 80));
label.setFont(new Font("Arial", Font.PLAIN, 14));
```

3. **ボタンサイズ**: Constantsで定義されたサイズを使用

```java
button.setPreferredSize(Constants.BUTTON_SIZE_MEDIUM);
```

## プロジェクト構造

```
src/main/java/com/study/form/
├── SurveyApp.java           # メインエントリーポイント
├── Constants.java           # 定数定義（色、サイズ、メッセージ）
├── model/                   # データモデル層
│   ├── Question.java        # 質問データ
│   ├── Response.java        # 回答データ
│   └── Config.java          # 設定データ
├── util/                    # ユーティリティ層
│   ├── ConfigManager.java   # 設定管理
│   ├── ActionLogger.java    # ログ記録
│   └── FileUtils.java       # ファイルI/O
└── ui/                      # UIコンポーネント層
    ├── MainWindow.java              # メイン画面
    ├── QuestionEditorWindow.java    # 問題作成画面
    ├── SurveyInterfaceWindow.java   # アンケート回答画面
    ├── SettingsWindow.java          # 設定画面
    └── ParticipantInfoWindow.java   # 被験者情報入力画面
```

### レイヤーの責任

1. **model/**: データ構造のみを定義。ビジネスロジックは含めない
2. **util/**: ビジネスロジックとデータ永続化を担当
3. **ui/**: ユーザーインターフェースのみを担当。ビジネスロジックはutilに委譲

## 新機能の追加方法

### 1. 新しい設定項目の追加

設定項目を追加する場合は、以下の手順で行ってください：

#### ステップ1: Config.javaにフィールドを追加

```java
// Config.java
private boolean newFeatureEnabled;

public boolean isNewFeatureEnabled() {
    return newFeatureEnabled;
}

public void setNewFeatureEnabled(boolean newFeatureEnabled) {
    this.newFeatureEnabled = newFeatureEnabled;
}
```

#### ステップ2: toMap()とfromMap()を更新

```java
// toMap()
map.put("new_feature_enabled", newFeatureEnabled);

// fromMap()
if (map.containsKey("new_feature_enabled"))
    this.newFeatureEnabled = (Boolean) map.get("new_feature_enabled");
```

#### ステップ3: デフォルト値を設定

```java
// Config.javaのコンストラクタ
public Config() {
    // ...
    this.newFeatureEnabled = false;
}
```

#### ステップ4: SettingsWindow.javaにUIを追加

```java
private JCheckBox newFeatureCheckBox;

// setupUI()内
newFeatureCheckBox = new JCheckBox("新機能を有効にする");
panel.add(createCheckBoxRow("新機能:", newFeatureCheckBox));

// loadCurrentSettings()内
newFeatureCheckBox.setSelected(config.isNewFeatureEnabled());

// saveSettings()内
config.setNewFeatureEnabled(newFeatureCheckBox.isSelected());
```

### 2. 新しいファイル名変数の追加

ファイル名フォーマットに新しい変数を追加する場合：

#### ステップ1: ConfigManager.formatFilename()を更新

```java
private String formatFilename(String format, String respondentId) {
    // ...
    String newVariable = "value"; // 新しい変数の値を取得

    String filename = format
        .replace("{date}", now.format(dateFormatter))
        .replace("{time}", now.format(timeFormatter))
        // ...
        .replace("{new_variable}", newVariable);  // 新しい変数を追加

    return filename;
}
```

#### ステップ2: README.mdとSettingsWindowのヘルプテキストを更新

```java
// SettingsWindow.java
JLabel helpLabel = new JLabel(
    "<html><i>使用可能な変数: {date}, {time}, {participant_name}, " +
    "{participant_id}, {sequence}, {new_variable}</i></html>"
);
```

### 3. 新しいログタイプの追加

#### ステップ1: ActionLogger.javaにメソッドを追加

```java
/**
 * 新しいアクションをログに記録します。
 *
 * @param questionNum 問題番号
 * @param detail 詳細情報
 */
public void logNewAction(int questionNum, String detail) {
    String timestamp = getTimestamp();
    String message = String.format("%s,新アクション種別,問題%d: %s",
        timestamp, questionNum, detail);
    writeLog(message);
}
```

## テストとビルド

### ビルドコマンド

```bash
# クリーンビルド
mvn clean package

# 依存関係を含むJAR生成
mvn clean package shade:shade

# コンパイルのみ
mvn compile

# 実行（開発時）
mvn exec:java -Dexec.mainClass="com.study.form.SurveyApp"
```

### 動作確認

新機能を追加した場合は、以下の項目を確認してください：

- [ ] アプリケーションが正常に起動する
- [ ] 新機能が期待通りに動作する
- [ ] 既存機能に影響がない
- [ ] 設定ファイル（config.json）が正しく保存・読み込みされる
- [ ] CSV/JSON形式でのデータ入出力が正常に動作する
- [ ] Python版との互換性が保たれている（ファイル形式）

### コードチェック

```bash
# Mavenでコンパイルエラーをチェック
mvn clean compile

# 警告も含めて確認
mvn clean compile -Xlint:all
```

## コミットガイドライン

### コミットメッセージのフォーマット

```
<種別>: <簡潔な説明>

<詳細な説明（任意）>
```

#### 種別

- `feat`: 新機能
- `fix`: バグ修正
- `docs`: ドキュメントのみの変更
- `style`: コードの動作に影響しない変更（フォーマット、セミコロン等）
- `refactor`: バグ修正や機能追加ではないコード変更
- `perf`: パフォーマンス改善
- `test`: テストの追加や修正
- `chore`: ビルドプロセスやツールの変更

#### 例

```
feat: 被験者情報入力機能を追加

アンケート開始前に被験者名とIDを入力できるダイアログを追加しました。
設定で機能のON/OFFが可能です。

- ParticipantInfoWindow.javaを新規作成
- Config.javaにuseParticipantInfoフィールドを追加
- ファイル名フォーマットに{participant_name}と{participant_id}変数を追加
```

```
fix: 選択肢ボタンのサイズが変わる問題を修正

理由を書き始めた時に選択肢ボタンのサイズが変わる問題を修正しました。
GridLayoutの使用とサイズ制約の明示的な設定により解決。

refs: SurveyInterfaceWindow.java:261
```

### プルリクエスト

プルリクエストを作成する際は、以下を含めてください：

1. **変更内容の説明**: 何を、なぜ変更したか
2. **テスト結果**: 動作確認した内容
3. **スクリーンショット**: UI変更の場合
4. **破壊的変更**: 既存機能への影響がある場合は明記

## よくある質問

### Q: 新しい依存ライブラリを追加したい

A: `pom.xml`に依存関係を追加してください。

```xml
<dependency>
    <groupId>group-id</groupId>
    <artifactId>artifact-id</artifactId>
    <version>version</version>
</dependency>
```

### Q: UIの色やサイズを変更したい

A: `Constants.java`の定数を変更してください。個別のクラスでハードコードしないでください。

### Q: ファイル形式を変更したい

A: Python版との互換性を保つため、CSV/JSON形式の変更は慎重に行ってください。

### Q: データベース対応を追加したい

A: `util/`パッケージに新しいクラスを作成し、既存のFileUtilsと同様のインターフェースを提供してください。

## 連絡先

質問や提案がある場合は、Issueを作成してください。

## ライセンス

本プロジェクトは研究用途で自由にご使用ください。
