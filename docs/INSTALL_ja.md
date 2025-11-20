# Form Response Logger インストールガイド

このガイドでは、Form Response Loggerのインストールと初期セットアップ方法を説明します。

## 目次

1. [システム要件](#システム要件)
2. [Javaのインストール](#javaのインストール)
3. [アプリケーションの入手](#アプリケーションの入手)
4. [ビルド方法](#ビルド方法)
5. [実行方法](#実行方法)
6. [初回セットアップ](#初回セットアップ)
7. [トラブルシューティング](#トラブルシューティング)

---

## システム要件

### 最小要件

- **OS**: Windows 10/11、macOS 10.14以降、Linux（Ubuntu 18.04以降推奨）
- **Java**: OpenJDK 11以降またはOracle JDK 11以降
- **RAM**: 512MB以上
- **ディスク空き容量**: 100MB以上
- **画面解像度**: 1024x768以上推奨

### 推奨要件

- **OS**: Windows 11、macOS 12以降、最新のLinuxディストリビューション
- **Java**: OpenJDK 17以降
- **RAM**: 1GB以上
- **ディスク空き容量**: 500MB以上
- **画面解像度**: 1920x1080以上

---

## Javaのインストール

### Javaがインストールされているか確認

ターミナル（Windows: コマンドプロンプト、macOS/Linux: ターミナル）を開いて、以下を実行：

```bash
java -version
```

**正常な出力例**:
```
openjdk version "17.0.1" 2021-10-19
OpenJDK Runtime Environment (build 17.0.1+12-39)
OpenJDK 64-Bit Server VM (build 17.0.1+12-39, mixed mode, sharing)
```

バージョンが11以降であれば、次のステップに進んでください。

### Javaのインストール（必要な場合）

#### Windows

##### 方法1: Chocolatey使用（推奨）

```powershell
# PowerShellを管理者権限で実行
choco install openjdk17
```

##### 方法2: 手動インストール

1. [Adoptium](https://adoptium.net/)にアクセス
2. 「Temurin 17 (LTS)」を選択
3. 「Windows」「x64」「JDK」を選択
4. インストーラーをダウンロードして実行
5. インストール時に「Set JAVA_HOME variable」にチェック

#### macOS

##### 方法1: Homebrew使用（推奨）

```bash
# Homebrewがインストールされていない場合
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# OpenJDKをインストール
brew install openjdk@17

# シンボリックリンクを作成
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

##### 方法2: 手動インストール

1. [Adoptium](https://adoptium.net/)にアクセス
2. 「Temurin 17 (LTS)」を選択
3. 「macOS」「x64」または「aarch64」（Apple Silicon）を選択
4. PKGファイルをダウンロードして実行

#### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Linux (Fedora/CentOS/RHEL)

```bash
sudo dnf install java-17-openjdk-devel
```

### 環境変数の設定

#### Windows

1. 「システムのプロパティ」→「環境変数」を開く
2. システム環境変数の「新規」をクリック
3. 変数名: `JAVA_HOME`
4. 変数値: Javaのインストールパス（例: `C:\Program Files\Eclipse Adoptium\jdk-17.0.1.12-hotspot`）
5. `Path`変数に`%JAVA_HOME%\bin`を追加

#### macOS/Linux

`~/.bashrc`または`~/.zshrc`に以下を追加：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH
```

変更を反映：
```bash
source ~/.bashrc  # または source ~/.zshrc
```

---

## アプリケーションの入手

### 方法1: ビルド済みJARファイルをダウンロード（簡単）

1. [Releases](https://github.com/yutoshima/form-response-logger/releases)ページにアクセス
2. 最新バージョンの`form-app-X.X.X.jar`をダウンロード
3. 任意のフォルダに保存

### 方法2: ソースコードからビルド

#### 前提条件

- Git
- Maven 3.6以降

#### Gitのインストール

**Windows**: [Git for Windows](https://gitforwindows.org/)をダウンロード

**macOS**:
```bash
brew install git
```

**Linux**:
```bash
sudo apt install git  # Ubuntu/Debian
sudo dnf install git  # Fedora/CentOS
```

#### Mavenのインストール

**Windows (Chocolatey)**:
```powershell
choco install maven
```

**macOS (Homebrew)**:
```bash
brew install maven
```

**Linux**:
```bash
sudo apt install maven  # Ubuntu/Debian
sudo dnf install maven  # Fedora/CentOS
```

#### Mavenのバージョン確認

```bash
mvn -version
```

---

## ビルド方法

### ソースコードの取得

```bash
# リポジトリをクローン
git clone https://github.com/yutoshima/form-response-logger.git

# プロジェクトディレクトリに移動
cd form-response-logger
```

### ビルド実行

```bash
# クリーンビルド
mvn clean package

# テストをスキップしてビルド（高速）
mvn clean package -DskipTests
```

**ビルド成功のメッセージ**:
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**生成されるファイル**:
```
target/form-app-1.0.0.jar
```

---

## 実行方法

### 方法1: 実行スクリプト使用（推奨）

#### Windows
```batch
run.bat
```

または、エクスプローラーで`run.bat`をダブルクリック

#### macOS/Linux
```bash
chmod +x run.sh  # 初回のみ
./run.sh
```

### 方法2: JARファイルから直接実行

```bash
java -jar target/form-app-1.0.0.jar
```

### 方法3: Maven経由で実行

```bash
mvn exec:java -Dexec.mainClass="com.study.form.SurveyApp"
```

### 実行オプション

#### メモリ割り当てを増やす

```bash
java -Xmx1G -jar target/form-app-1.0.0.jar
```

#### ログレベルを変更

```bash
java -Djava.util.logging.level=FINE -jar target/form-app-1.0.0.jar
```

---

## 初回セットアップ

### 自動生成されるファイルとフォルダ

初回起動時、以下が自動的に作成されます：

```
プロジェクトフォルダ/
├── config.json                    # アプリケーション設定
└── data/
    ├── questions/                 # 問題ファイル格納
    │   └── sample_questions.csv   # サンプル問題
    ├── responses/                 # 回答ファイル格納
    └── logs/                      # ログファイル格納
```

### config.jsonの初期内容

```json
{
  "questions_directory": "data/questions",
  "questions_file": "sample_questions.csv",
  "log_directory": "data/logs",
  "log_name_format": "action_log_{respondent_id}_{date}.csv",
  "response_directory": "data/responses",
  "response_name_format": "responses_{respondent_id}_{date}.csv",
  "output_format": "csv",
  "default_choices": 4,
  "choice_columns": 2,
  "auto_save": true,
  "use_participant_info": true,
  "log_sequence": 1,
  "response_sequence": 1
}
```

### サンプル問題の確認

`data/questions/sample_questions.csv`に以下のようなサンプルが含まれています：

```csv
問題番号,質問文,選択肢1,選択肢2,選択肢3,選択肢4
1,好きな色は何ですか？,赤,青,緑,黄色
2,好きな季節は何ですか？,春,夏,秋,冬
```

### 初回動作確認

1. アプリケーションを起動
2. **「アンケートに回答」**をクリック
3. サンプル問題が表示されることを確認
4. 1問だけ回答してみる
5. `data/responses/`に回答ファイルが生成されることを確認

---

## トラブルシューティング

### 起動時のエラー

#### エラー: "java: command not found"

**原因**: Javaがインストールされていないか、パスが通っていない

**解決策**:
1. Javaをインストール（上記参照）
2. 環境変数`PATH`に`JAVA_HOME/bin`が含まれているか確認

#### エラー: "UnsupportedClassVersionError"

**原因**: Javaのバージョンが古い

**解決策**:
```bash
java -version  # バージョン確認
```
Java 11以降にアップグレード

#### エラー: "Main class not found"

**原因**: JARファイルが破損しているか、正しくビルドされていない

**解決策**:
```bash
mvn clean package  # 再ビルド
```

### ビルド時のエラー

#### エラー: "mvn: command not found"

**原因**: Mavenがインストールされていない

**解決策**: Mavenをインストール（上記参照）

#### エラー: "Compilation failure"

**原因**: ソースコードの問題またはJavaバージョンの不一致

**解決策**:
```bash
# Javaバージョン確認
java -version
javac -version

# pom.xmlの<maven.compiler.source>と<maven.compiler.target>を確認
```

### 実行時のエラー

#### エラー: "Could not create directory"

**原因**: 書き込み権限がない

**解決策**:
```bash
# ディレクトリの権限確認
ls -la data/

# 権限を付与（Linux/macOS）
chmod -R 755 data/
```

#### 問題: 日本語が文字化けする

**原因**: エンコーディングの問題

**解決策**:
```bash
# UTF-8エンコーディングで起動
java -Dfile.encoding=UTF-8 -jar target/form-app-1.0.0.jar
```

#### 問題: 画面が正しく表示されない

**原因**: JavaのLook and Feelの問題

**解決策**:
1. Javaを最新版にアップデート
2. システムのディスプレイ設定を確認（スケーリング設定など）

---

## アンインストール

### 完全削除手順

1. アプリケーションを終了
2. プロジェクトフォルダ全体を削除
3. 必要に応じて、保存した回答データとログを別の場所にバックアップ

**重要**: `data/responses/`と`data/logs/`内のデータは削除前に必ずバックアップしてください。

---

## 次のステップ

インストールが完了したら：

1. **[ユーザーマニュアル](USER_MANUAL_ja.md)**で詳しい使い方を確認
2. **[README](../README_ja.md)**でプロジェクト概要を確認
3. 問題を作成して、実際にアンケートを実施してみる

---

## サポート

問題が解決しない場合：

- **Issue報告**: https://github.com/yutoshima/form-response-logger/issues
- **ディスカッション**: https://github.com/yutoshima/form-response-logger/discussions

---

**最終更新**: 2025年1月
