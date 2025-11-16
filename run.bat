@echo off
REM 研究用アンケートシステム起動スクリプト (Windows)

cd /d %~dp0

REM Mavenがインストールされているか確認
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Mavenがインストールされていません。
    echo https://maven.apache.org/install.html からインストールしてください。
    pause
    exit /b 1
)

REM 初回ビルド
if not exist "target\form-app-1.0.0.jar" (
    echo 初回ビルドを実行します...
    call mvn clean package
)

REM アプリケーションを起動
echo アプリケーションを起動します...
java -jar target\form-app-1.0.0.jar

pause
