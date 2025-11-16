#!/bin/bash

# 研究用アンケートシステム起動スクリプト

cd "$(dirname "$0")"

# Mavenがインストールされているか確認
if ! command -v mvn &> /dev/null
then
    echo "Mavenがインストールされていません。"
    echo "https://maven.apache.org/install.html からインストールしてください。"
    exit 1
fi

# 初回ビルド
if [ ! -f "target/form-app-1.0.0.jar" ]; then
    echo "初回ビルドを実行します..."
    mvn clean package
fi

# アプリケーションを起動
echo "アプリケーションを起動します..."
java -jar target/form-app-1.0.0.jar
