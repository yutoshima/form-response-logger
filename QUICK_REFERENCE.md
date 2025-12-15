# クイックリファレンス

## 🚀 よく使うコマンド

### ビルドとテスト
```bash
# プロジェクトのビルド
mvn clean package

# テスト実行（カバレッジレポート付き）
mvn test
open target/site/jacoco/index.html

# テストスキップでビルド
mvn clean package -DskipTests
```

### コード品質チェック
```bash
# 全チェック実行
mvn clean verify

# 個別チェック
mvn spotbugs:check      # バグ検出
mvn pmd:check           # コード品質
mvn checkstyle:check    # コーディング規約

# レポート生成
mvn site
open target/site/index.html
```

### データ移行
```bash
# CSVからSQLiteへ移行
java -cp target/form-app-1.0.0.jar form.database.MigrationApp

# カスタムパスで移行
java -cp target/form-app-1.0.0.jar form.database.MigrationApp \
  data/survey.db data/questions data/responses data/logs
```

### データエクスポート
```bash
# CSVエクスポート
java -cp target/form-app-1.0.0.jar form.database.export.ExportApp \
  data/survey.db csv export/data

# JSONエクスポート
java -cp target/form-app-1.0.0.jar form.database.export.ExportApp \
  data/survey.db json export/data

# Excelエクスポート
java -cp target/form-app-1.0.0.jar form.database.export.ExportApp \
  data/survey.db excel export/data
```

### アプリケーション実行
```bash
# GUIアプリケーション起動
java -jar target/form-app-1.0.0.jar
```

## 📂 重要なファイル

```
プロジェクト/
├── pom.xml                          # Maven設定
├── src/main/resources/logback.xml   # ログ設定
├── data/
│   ├── survey.db                    # SQLiteデータベース
│   ├── questions/                   # 質問CSVファイル
│   ├── responses/                   # 回答CSVファイル
│   └── logs/                        # アクションログCSV
├── logs/
│   ├── application.log              # 全般ログ
│   ├── database.log                 # DBログ
│   └── error.log                    # エラーログ
└── target/
    ├── form-app-1.0.0.jar          # 実行可能JAR
    └── site/
        ├── jacoco/index.html        # カバレッジレポート
        ├── spotbugs.html            # バグ検出レポート
        ├── pmd.html                 # コード品質レポート
        └── checkstyle.html          # スタイルレポート
```

## 📊 カバレッジレポート

```bash
# テスト実行してカバレッジ計測
mvn test

# レポート表示
open target/site/jacoco/index.html
```

**カバレッジ目標:** 60%以上

## 🔧 設定変更

### ログレベル変更
`src/main/resources/logback.xml`を編集:

```xml
<!-- DEBUGモードに変更 -->
<logger name="form.database" level="DEBUG"/>
<root level="DEBUG">
```

### カバレッジ目標変更
`pom.xml`の JaCoCo設定を編集:

```xml
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.80</minimum>  <!-- 80%に変更 -->
</limit>
```

## 💡 トラブルシューティング

### ビルドエラー
```bash
# 依存関係を再ダウンロード
mvn clean install -U

# キャッシュクリア
rm -rf ~/.m2/repository
mvn clean install
```

### データベースロック
```bash
# データベース接続確認
sqlite3 data/survey.db "SELECT COUNT(*) FROM questions;"

# データベース再作成
rm data/survey.db
java -cp target/form-app-1.0.0.jar form.database.MigrationApp
```

### ログファイルが大きい
```bash
# 古いログ削除
rm logs/*.log.*

# ログ設定で保持日数変更（logback.xml）
<maxHistory>7</maxHistory>  <!-- 7日間に変更 -->
```

## 📈 パフォーマンスチューニング

### バッチサイズ調整
```java
// 大量データ挿入時
BatchInsertUtil batchUtil = new BatchInsertUtil(dbService);
int count = batchUtil.batchInsertResponses(responses, 1000); // バッチサイズ1000
```

### トランザクション使用
```java
// 複数操作を1トランザクションで
TransactionManager txManager = new TransactionManager(dbService);
txManager.executeInTransaction(() -> {
    // 複数のDB操作
});
```

## 🎯 開発ワークフロー

1. **コード変更**
   ```bash
   # 変更後、テスト実行
   mvn test
   ```

2. **コード品質チェック**
   ```bash
   mvn spotbugs:check pmd:check checkstyle:check
   ```

3. **カバレッジ確認**
   ```bash
   open target/site/jacoco/index.html
   ```

4. **ビルド**
   ```bash
   mvn clean package
   ```

5. **動作確認**
   ```bash
   java -jar target/form-app-1.0.0.jar
   ```

## 📚 ドキュメント

- **包括的改善サマリー**: `COMPREHENSIVE_IMPROVEMENTS_SUMMARY.md`
- **JUnit & SQLite**: `JUNIT_SQLITE_IMPLEMENTATION_SUMMARY.md`
- **データベース移行**: `DATABASE_MIGRATION_GUIDE.md`
- **このリファレンス**: `QUICK_REFERENCE.md`

## 🆘 ヘルプ

問題が発生した場合:
1. ログファイルを確認（`logs/error.log`）
2. カバレッジレポートで未テストコードを確認
3. コード品質レポートで潜在的問題を確認
4. GitHubのissueを作成

---

**最終更新**: 2025年12月15日
**バージョン**: 1.0.0
**Java**: 21
**ビルドツール**: Maven 3.x
