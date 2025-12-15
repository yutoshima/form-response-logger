# 研究用アンケート・穴埋めシステム

研究・教育用のJavaアプリケーション。フォーム回答システムと穴埋め学習アプリの2つのツールを提供します。

## 📋 目次

- [システム概要](#システム概要)
- [必要環境](#必要環境)
- [インストール](#インストール)
- [1. フォームシステム（SurveyApp）](#1-フォームシステムsurveyapp)
- [2. 穴埋めシステム（FillInBlankApp）](#2-穴埋めシステムfillinblankapp)
- [プロジェクト構造](#プロジェクト構造)
- [研究実験ガイド](#研究実験ガイド)
- [ライセンス](#ライセンス)

## システム概要

このプロジェクトは2つの独立したアプリケーションを含みます：

1. **SurveyApp**: 研究用アンケートシステム（Google Forms風のUI）
2. **FillInBlankApp**: マークダウン穴埋め問題アプリ（答え合わせ・ヒント機能付き）

## 必要環境

- **Java**: 21以上
- **Maven**: 3.6以上

## インストール

```bash
# リポジトリをクローン
git clone <repository-url>
cd study_form_app_java_2

# ビルド
mvn clean package

# 実行（下記の各アプリを参照）
```

---

## 1. フォームシステム（SurveyApp）

研究用のアンケート回答・作成システム。詳細なアクションログ機能を備えています。

### 主な機能

- ✅ **GUI問題エディタ**: 質問と選択肢を作成
- ✅ **HTML対応**: HTMLタグで装飾可能
- ✅ **理由記録**: 回答の理由を記録（テキスト入力・音声録音）
- ✅ **音声録音機能**: 理由の音声録音とThink-aloud録音に対応
- ✅ **完全なアクションログ**: すべての操作をタイムスタンプ付きで記録
- ✅ **CSV/JSON出力**: データをエクスポート
- ✅ **FlatLafテーマ**: モダンなUI

### 起動方法

```bash
# 方法1: スクリプトで起動
./run.sh           # macOS/Linux
run.bat            # Windows

# 方法2: Mavenで起動
mvn exec:java -Dexec.mainClass="form.SurveyApp"

# 方法3: JARファイルから起動
java -jar target/form-app-1.0.0.jar
```

### 使い方

1. **問題作成**: 「問題を作成」→ 質問と選択肢を入力 → 保存（CSV/JSON）
2. **アンケート回答**: 「アンケートに回答」→ 質問に答える → 理由を記入
3. **設定**: ⚙ボタンで参加者情報、ファイルパス、レイアウトなどをカスタマイズ

### 録音機能

**理由の音声録音**
- 設定で「理由の音声録音」を有効化
- 回答画面で選択肢を選択後、理由入力エリアに「🎤 録音開始」ボタンが表示
- ボタンをクリックして録音開始/停止

**Think-aloud録音**
- 設定で「Think-aloud録音」を有効化
- 回答画面のヘッダーに「🎤 Think-aloud録音開始」ボタンが表示
- 選択肢を選んでいる間の思考プロセスを録音

録音ファイルは `data/audio/` に保存されます（設定で変更可能）：
- 理由録音: `reason_q{問題番号}_{参加者ID}_{タイムスタンプ}.wav`
- Think-aloud: `thinkaloud_q{問題番号}_{参加者ID}_{タイムスタンプ}.wav`

### データ保存先

- **質問**: `data/questions/`
- **回答**: `data/responses/`
- **ログ**: `data/logs/`
- **録音**: `data/audio/`
- **設定**: `config.json`

---

## 2. 穴埋めシステム（FillInBlankApp）

マークダウンファイルの穴埋め問題（`______`）を解くためのGUIアプリ。

### 主な機能

- 🎨 **リッチプレビュー**: HTML/CSSによる美しいマークダウン表示
- 📄 **HTML出力**: 完成版をHTMLファイルとして保存（Ctrl+H）
- 📊 **GitHub Flavored Markdown対応**: テーブル、コードブロックなど
- 🎯 **コードハイライト**: ダークテーマで見やすい
- ✅ **答え合わせ機能**: 正解と比較（緑=正解、赤=不正解）
- 💡 **ヒント機能**: 各穴埋めにヒントボタン

### 起動方法

```bash
# 方法1: スクリプトで起動
./run_fillblank.sh

# 方法2: VSCodeから
# F5 → "FillInBlankApp (穴埋めアプリ)" を選択
```

### 使い方

1. **ファイルを開く**: Ctrl+O → `steps/samples/test_fillblank.md`
2. **ヒント表示**: 各入力欄の「💡」ボタン
3. **答えを入力**: 右側の入力欄に記入
4. **答え合わせ**: 「✓ 答え合わせ」ボタン → 正誤判定
5. **保存**: Ctrl+S（マークダウン）または Ctrl+H（HTML）

### マークダウンファイルの書式

```markdown
# 問題タイトル

## 問題1
\`\`\`java
public class ______ {
    public static ______ main(String[] args) {
        System.out.println("______");
    }
}
\`\`\`

---

## 正解

1. String（または任意のクラス名）
2. void
3. Hello World（または任意の文字列）

## ヒント

1. Javaのクラス名は大文字で始まります
2. mainメソッドの戻り値型です
3. 画面に表示したい文字列を入力してください
```

### サンプルファイル

- `steps/samples/test_fillblank.md` - Java基礎（12問）
- `steps/samples/example_survey.md` - SurveyApp開発（24問）

---

## プロジェクト構造

```
study_form_app_java_2/
├── README.md                    # このファイル
├── pom.xml                      # Maven設定
├── run.sh / run.bat             # SurveyApp起動スクリプト
├── run_fillblank.sh             # FillInBlankApp起動スクリプト
│
├── src/main/java/form/          # SurveyApp（フォームシステム）
│   ├── SurveyApp.java           # メインクラス
│   ├── model/                   # データモデル
│   ├── ui/                      # UIコンポーネント
│   └── util/                    # ユーティリティ
│
├── tools/                       # FillInBlankApp（穴埋めシステム）
│   ├── FillInBlankApp.java      # メインクラス
│   └── README.md                # 詳細ドキュメント
│
├── steps/samples/               # 穴埋め問題サンプル
│   ├── test_fillblank.md
│   └── example_survey.md
│
└── data/                        # SurveyAppのデータ（自動生成）
    ├── questions/               # 質問ファイル
    ├── responses/               # 回答ファイル
    └── logs/                    # ログファイル
```

## 技術スタック

- **言語**: Java 21
- **ビルドツール**: Maven
- **GUIフレームワーク**: Java Swing
- **Look and Feel**: [FlatLaf](https://www.formdev.com/flatlaf/) 3.2.5
- **JSONライブラリ**: [Gson](https://github.com/google/gson) 2.10.1
- **マークダウンパーサー**: [CommonMark](https://commonmark.org/) 0.21.0

---

## 研究実験ガイド

このシステムを使用した研究実験の実施方法を詳しく説明しています。

### 📚 ドキュメント

- **[RESEARCH_PLAN.md](RESEARCH_PLAN.md)**: 研究全体の計画（前期学習フェーズ + 後期実験フェーズ）
- **[EXPERIMENT_GUIDE.md](EXPERIMENT_GUIDE.md)**: 3つの実験の詳細手順（担当者別）
- **[SETUP_GUIDE.md](SETUP_GUIDE.md)**: 実験セットアップと実施ガイド

### 🎯 実験内容

#### 実験1: 回答時間と理解度の関係検証
- **担当者A**: 得意分野での回答時間と理由の具体性を分析
- **被験者**: 20名（数学得意10名、英語得意10名）
- **期間**: 3週間

#### 実験2: 理由の記述 vs 録音の比較
- **担当者B**: テキスト入力と音声録音の比較分析
- **被験者**: 20名（全員が両条件を経験）
- **期間**: 2週間

#### 実験3: Think-aloud法の効果
- **担当者C**: 思考発話プロトコルの分析
- **被験者**: 20名（Think-aloud有10名、無10名）
- **期間**: 2週間

### 📖 前期学習カリキュラム（10週間）

Java初心者向けの学習プログラム：

| 週 | テーマ | 問題数 |
|:---:|:---|:---:|
| 1 | 開発環境とGit入門 | 8問 |
| 2 | Java基礎1: 変数とデータ型 | 12問 |
| 3 | Java基礎2: 制御構文 | 15問 |
| 4 | Java基礎3: 配列とメソッド | 15問 |
| 5 | オブジェクト指向1: クラス | 12問 |
| 6 | オブジェクト指向2: 継承 | 12問 |
| 7 | オブジェクト指向3: インターフェース | 12問 |
| 8 | 例外処理とファイルI/O | 12問 |
| 9 | コレクションフレームワーク | 15問 |
| 10 | 総合演習とまとめ | 20問 |

各週の教材は `steps/curriculum/` に格納されています。

### 🔬 実験問題セット

実験用の問題は `steps/experiments/` に格納されています：

- `exp1_math_problems.md`: 実験1用の数学問題（10問）
- `exp1_english_problems.md`: 実験1用の英語問題（10問）
- その他、実験2・3用の問題セット

### 📊 データ分析

Python分析スクリプトは `analysis/` に格納されています：

```bash
cd analysis/
python analyze_experiment1.py
python analyze_experiment2.py
python analyze_experiment3.py
```

---

## ライセンス

MIT License - 詳細は [LICENSE](LICENSE) ファイルを参照してください。

## 謝辞

- [FlatLaf](https://www.formdev.com/flatlaf/) - モダンなUI
- [Gson](https://github.com/google/gson) - JSON処理
- [CommonMark](https://commonmark.org/) - マークダウンレンダリング
