# Form Response Logger 開発者ガイド

Form Response Loggerへの貢献に興味を持っていただきありがとうございます！このドキュメントでは、プロジェクトへの貢献に関するガイドラインを提供します。

[English](../CONTRIBUTING.md) | 日本語

## 目次

1. [はじめに](#はじめに)
2. [開発環境のセットアップ](#開発環境のセットアップ)
3. [コーディングスタイル](#コーディングスタイル)
4. [プロジェクト構造](#プロジェクト構造)
5. [最近の更新](#最近の更新)
6. [変更の作成](#変更の作成)
7. [プルリクエストの提出](#プルリクエストの提出)

---

## はじめに

### 前提条件

- **Java**: OpenJDK 11以上
- **Maven**: 3.6以上
- **Git**: 最新版
- **IDE**: IntelliJ IDEA、Eclipse、VS Code（推奨だがオプション）

### 開発環境のセットアップ

```bash
# リポジトリをフォークしてクローン
git clone https://github.com/your-username/form-response-logger.git
cd form-response-logger

# プロジェクトをビルド
mvn clean install

# アプリケーションを実行
mvn exec:java -Dexec.mainClass="com.study.form.SurveyApp"
```

### IDE設定

#### IntelliJ IDEA
1. プロジェクトディレクトリを開く（`File` → `Open`）
2. Mavenプロジェクトが自動検出されます
3. 実行構成を設定：
   - メインクラス: `com.study.form.SurveyApp`

#### Eclipse
1. プロジェクトをインポート: `File` → `Import` → `Maven` → `Existing Maven Projects`
2. プロジェクトディレクトリを選択

---

## コーディングスタイル

### 一般原則

1. **可読性優先**: コードは書かれるより読まれることが多い
2. **DRY原則**: Don't Repeat Yourself（繰り返しを避ける）
3. **単一責任**: 各クラス/メソッドは1つの責任のみを持つ
4. **定数の使用**: マジックナンバーや文字列は使わず、`Constants.java`を使用

### Java命名規則

```java
// クラス: PascalCase
public class ConfigManager { }

// メソッド: camelCase
public void loadConfig() { }

// 変数: camelCase
private String questionsFile;

// 定数: UPPER_SNAKE_CASE
public static final String CONFIG_FILE = "config.json";

// パッケージ: 小文字
package com.study.form.util;
```

### コードフォーマット

- **インデント**: スペース4つ
- **行の長さ**: 100文字推奨（最大120文字）
- **ブレース**: K&Rスタイル（開きブレースは同じ行）
- **スペース**: キーワードの後、開きブレースの前

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

### Javadoc

すべてのpublicクラスとメソッドにJavadocコメントを付ける：

```java
/**
 * アプリケーション設定を管理します。
 *
 * <p>このクラスは、config.jsonに保存されているアプリケーションの
 * 設定の読み込み、保存、管理を担当します。</p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public class ConfigManager {

    /**
     * ファイルから設定を読み込みます。
     *
     * <p>ファイルが存在しないか無効な場合、デフォルト設定を作成します。</p>
     */
    private void loadConfig() {
        // 実装
    }
}
```

---

## プロジェクト構造

```
src/main/java/com/study/form/
├── SurveyApp.java           # メインエントリーポイント
├── Constants.java           # 集中管理された定数（45以上の定数）
├── model/                   # データモデル
│   ├── Question.java        # 問題と選択肢
│   ├── Response.java        # アンケート回答データ
│   └── Config.java          # アプリケーション設定
├── util/                    # ビジネスロジックユーティリティ
│   ├── ConfigManager.java   # 設定ファイル管理
│   ├── ActionLogger.java    # ユーザーアクションログ
│   └── FileUtils.java       # CSV/JSON入出力
└── ui/                      # ユーザーインターフェース（Swing）
    ├── MainWindow.java
    ├── QuestionEditorWindow.java
    ├── SurveyInterfaceWindow.java
    ├── SettingsWindow.java
    └── ParticipantInfoWindow.java
```

### レイヤーの責任

- **Model**: データ構造のみ、ビジネスロジックなし
- **Util**: ビジネスロジック、ファイルI/O、データ処理
- **UI**: ユーザーインターフェース、イベント処理、最小限のロジック

---

## 最近の更新

### 最新機能（2025年）

- **問題のHTML対応**: リッチテキスト書式設定のためのHTMLタグをサポート
  - 太字（`<b>`）、斜体（`<i>`）、下線（`<u>`）
  - 改行（`<br>`）、段落（`<p>`）
  - カスタム書式設定のためのCSSスタイル
  - 例は`data/questions/html_test.csv`を参照

- **問題エディターの改善**:
  - ダブルクリックで既存の問題を編集
  - 視覚的インジケーター付きの編集モード
  - 編集をキャンセルするためのキャンセルボタン

- **UI改善**:
  - 長いコンテンツのための縦スクロール対応の問題文
  - 理由入力欄を6行から4行に削減
  - 適切なフォントスタイルでのHTML描画

- **ログ機能の向上**:
  - デバッグ用の詳細なコンソール出力
  - ログディレクトリとファイル作成メッセージ

- **設定管理**:
  - 問題ファイルパスの保存を修正（ディレクトリとファイル名を分割）
  - 設定ウィンドウでの適切なフルパス処理

### HTML/CSSサポート

問題表示は`JEditorPane`でHTML対応しています。利用可能なタグとスタイル：

```html
<!-- 基本書式 -->
<b>太字</b> または <strong>太字</strong>
<i>斜体</i> または <em>斜体</em>
<u>下線</u>

<!-- 構造 -->
<br> 改行
<p>段落</p>
<h1>見出し1</h1> から <h6>見出し6</h6>

<!-- リスト -->
<ul><li>順序なしリスト項目</li></ul>
<ol><li>順序付きリスト項目</li></ol>

<!-- スタイリング -->
<font color="red">色付きテキスト</font>
<span style="color: blue; font-size: 14pt;">スタイル付きテキスト</span>

<!-- styleタグでCSS -->
<html>
  <head>
    <style>
      body { font-family: 'Yu Gothic'; font-size: 16pt; }
      .highlight { background-color: yellow; }
    </style>
  </head>
  <body>
    <p class="highlight">ハイライト段落</p>
  </body>
</html>
```

---

## 変更の作成

### 新機能の追加

#### 1. 新しい設定の追加

**ステップ1**: `Config.java`を更新

```java
private String newSetting;

public String getNewSetting() { return newSetting; }
public void setNewSetting(String value) { newSetting = value; }

// toMap()とfromMap()メソッドを更新
```

**ステップ2**: `Constants.java`に定数を追加（必要な場合）

```java
public static final String DEFAULT_NEW_SETTING = "default_value";
```

**ステップ3**: `SettingsWindow.java`を更新してUIコンポーネントを追加

#### 2. 新しいファイル名変数の追加

**ステップ1**: `ConfigManager.formatFilename()`を更新

```java
filename = filename.replace("{new_variable}", actualValue);
```

**ステップ2**: READMEと設定のヘルプテキストに文書化

#### 3. 新しいログアクションの追加

**ステップ1**: `Constants.java`に定数を追加

```java
public static final String LOG_ACTION_NEW_EVENT = "新しいイベント";
```

**ステップ2**: `ActionLogger`を使用して記録

```java
actionLogger.logAction(Constants.LOG_ACTION_NEW_EVENT, "詳細情報");
```

### エラーハンドリング

常に特定の例外型を使用：

```java
// 良い例
try (Writer writer = createUTF8Writer(filepath)) {
    gson.toJson(data, writer);
    return true;
} catch (IOException e) {
    System.err.println("保存に失敗しました: " + e.getMessage());
    return false;
} catch (Exception e) {
    System.err.println("予期しないエラー: " + e.getMessage());
    return false;
}

// 悪い例
try {
    // 操作
} catch (Exception e) {
    e.printStackTrace();
}
```

### Null安全性

常にパラメータを検証：

```java
public String getLogPath(String respondentId) {
    if (respondentId == null) {
        throw new IllegalArgumentException("respondentIdがnullです");
    }
    return formatFilePath(config.getLogDirectory(), config.getLogNameFormat(), respondentId);
}
```

---

## プルリクエストの提出

### 提出前のチェック

1. **ビルドが成功する**: `mvn clean package`
2. **コードがスタイルガイドに従っている**
3. **Javadocが完成している**
4. **IDEで警告がない**
5. **UI変更を追加する場合は手動でテスト**

### コミットメッセージ

明確で説明的なコミットメッセージを使用：

```
良い例:
- 設定ウィンドウに連番編集機能を追加
- 回答のCSVエスケープ処理を修正
- FileUtilsのエラーハンドリングを改善

悪い例:
- 更新
- バグ修正
- 変更
```

### プルリクエストのプロセス

1. **フォーク** リポジトリをフォーク
2. **ブランチ作成**: `git checkout -b feature/your-feature-name`
3. **変更作成** 上記のガイドラインに従って変更
4. **コミット** 明確なメッセージでコミット
5. **プッシュ**: `git push origin feature/your-feature-name`
6. **プルリクエスト** 変更の説明を含めてプルリクエストを開く

### PRの説明テンプレート

```markdown
## 説明
このPRが行うことの簡潔な説明

## 変更内容
- 変更1
- 変更2

## テスト
これらの変更をどのようにテストしたか

## スクリーンショット（UI変更の場合）
関連する場合はスクリーンショットを追加
```

---

## コード品質ガイドライン

### 現在の指標（維持または改善）

- Javadocカバレッジ: 95%以上
- マジックナンバー: 0（Constants.javaを使用）
- コード重複: 最小限
- 平均メソッド長: 20行未満
- 最大メソッド長: 40行未満

### ベストプラクティス

1. **定数の使用**: すべてのUIサイズ、色、メッセージは`Constants.java`に
2. **メソッドの抽出**: 長いメソッドを小さく焦点を絞ったものに分割
3. **意味のある名前**: 変数とメソッドは自己文書化されるべき
4. **コメント**: "なぜ"を説明、"何を"ではない（コードが"何を"を示す）
5. **リソース管理**: I/Oには常にtry-with-resourcesを使用

---

## テスト

### 手動テスト

新機能を追加する場合：

1. すべての画面で機能をテスト
2. エッジケースをテスト（空の入力、大きなファイルなど）
3. 異なるOS設定でテスト（可能な場合）
4. データファイル（CSV/JSON）の出力を確認

### テストチェックリスト

- [ ] アプリケーションが正常に起動する
- [ ] すべてのボタンとメニューが機能する
- [ ] ファイルの読み込みと保存が正しく動作する
- [ ] エラーメッセージが適切に表示される
- [ ] 日本語が正しく表示される（文字化けなし）
- [ ] ログファイルが正しく生成される

---

## ドキュメント

### ドキュメントの更新

コードを変更する場合、関連するドキュメントも更新してください：

- **README.md / README_ja.md**: 新機能の概要
- **USER_MANUAL_ja.md**: ユーザー向けの詳細な説明
- **CONTRIBUTING.md / CONTRIBUTING_ja.md**: 開発者向けガイドライン
- **Javadoc**: コード内のドキュメント

### ドキュメントの種類

| ファイル | 対象読者 | 内容 |
|---------|---------|------|
| README | すべて | プロジェクト概要、クイックスタート |
| USER_MANUAL_ja.md | エンドユーザー | 詳細な使用方法 |
| INSTALL_ja.md | エンドユーザー | インストール手順 |
| CONTRIBUTING_ja.md | 開発者 | 開発ガイドライン |

---

## 質問がありますか？

お気軽にIssueを開いてください：

- バグ報告
- 機能リクエスト
- 貢献に関する質問
- 改善の提案

Form Response Loggerへの貢献ありがとうございます！

---

**最終更新**: 2025年1月
