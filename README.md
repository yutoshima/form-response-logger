# Form Response Logger

A desktop survey application for research with comprehensive logging capabilities.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

## Overview

Form Response Logger is a desktop application designed for conducting research surveys with detailed action logging. Built with Java Swing and FlatLaf, it provides a modern, user-friendly interface similar to Google Forms while offering extensive logging capabilities for research data integrity.

## Key Features

### Survey Creation & Management
- **GUI-based Question Editor**: Create questions and choices with an intuitive interface
- **Flexible Question Management**: Reorder and delete questions easily
- **Multiple Export Formats**: Save questions as CSV or JSON
- **Auto-loading**: Automatically load question files from configured directories

### Response Collection
- **Visual Feedback**: Selected choices are highlighted for clarity
- **Reason Recording**: Require respondents to explain their choices
- **Response Modification Rules**: Prevent impulsive changes after reasoning begins
- **Complete Action Logging**: All user actions are timestamped and recorded

### Data Management
- **Participant Information**: Collect and manage participant names and IDs
- **Flexible File Naming**: Auto-generate filenames with dates, times, and participant info
- **Sequence Management**: Independently manage sequence numbers for logs and responses
- **Dual Format Export**: Export data in CSV (Excel-compatible UTF-8 BOM) and/or JSON

### Customization
- **Layout Options**: Display choices in 1-4 columns
- **Appearance**: Light/dark mode, color themes, font size customization
- **Configurable Paths**: Set custom directories for questions, responses, and logs

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Installation & Running

### Build from Source

```bash
# Clone the repository
git clone https://github.com/your-username/form-response-logger.git
cd form-response-logger

# Build with Maven
mvn clean package

# Run the application
java -jar target/form-app-1.0.0.jar
```

### Quick Start Scripts

```bash
# macOS/Linux
./run.sh

# Windows
run.bat
```

### Run with Maven

```bash
mvn exec:java -Dexec.mainClass="com.study.form.SurveyApp"
```

## Quick Start Guide

### 1. First Launch

On first launch, the application automatically creates:
- `data/questions/` - Question files directory
- `data/responses/` - Response files directory
- `data/logs/` - Log files directory
- `config.json` - Configuration file
- `data/questions/sample_questions.csv` - Sample questions

### 2. Create Questions

1. Click **"問題を作成" (Create Questions)**
2. Enter question text and choices (minimum 2 choices)
3. Click **"問題を追加" (Add Question)**
4. Use **↑/↓** buttons to reorder, **✕** to delete
5. Click **"保存" (Save)** and choose CSV or JSON format

### 3. Configure Settings (Optional)

Click **"⚙ 設定" (Settings)** to configure:

- **Participant Information**: Enable/disable participant name and ID collection
- **File Paths**: Set directories for questions, responses, and logs
- **Output Format**: Choose CSV, JSON, or both
- **Layout**: Set number of choice columns (1-4)
- **Sequence Numbers**: Manage auto-incrementing file counters

**Filename Template Variables:**
- `{date}` - Date (YYYYMMDD)
- `{time}` - Time (HHMMSS)
- `{respondent_id}` - Auto-generated 8-character UUID
- `{participant_name}` - Participant name from settings
- `{participant_id}` - Participant ID from settings
- `{sequence}` - Auto-incrementing 3-digit number (001, 002, ...)

**Example:** `responses_{participant_name}_{sequence}.csv` → `responses_田中太郎_001.csv`

### 4. Conduct Survey

1. Click **"アンケートに回答" (Take Survey)**
2. Enter participant information (if enabled)
3. For each question:
   - Select a choice (highlighted in green)
   - Write your reason
   - Click **"次の問題へ" (Next Question)**
4. Responses are automatically saved at the end

## Response Modification Rules

To ensure data integrity:
- **Before writing a reason**: Choice can be changed freely
- **After writing begins**: Choice becomes locked
- **To change choice**: Click **"理由を書き直す" (Rewrite Reason)** to unlock

This mechanism encourages thoughtful responses and prevents impulsive changes.

## Project Structure

```
form-response-logger/
├── README.md
├── CONTRIBUTING.md
├── LICENSE
├── pom.xml
├── .gitignore
├── run.sh / run.bat
├── src/main/java/com/study/form/
│   ├── SurveyApp.java          # Main application entry
│   ├── Constants.java           # Centralized constants
│   ├── model/                   # Data models
│   │   ├── Question.java
│   │   ├── Response.java
│   │   └── Config.java
│   ├── util/                    # Utilities
│   │   ├── ConfigManager.java
│   │   ├── ActionLogger.java
│   │   └── FileUtils.java
│   └── ui/                      # User interface
│       ├── MainWindow.java
│       ├── QuestionEditorWindow.java
│       ├── SurveyInterfaceWindow.java
│       ├── SettingsWindow.java
│       └── ParticipantInfoWindow.java
└── data/                        # Auto-generated at runtime
    ├── questions/
    ├── responses/
    └── logs/
```

## Output Formats

### Questions (CSV)
```csv
問題番号,質問文,選択肢1,選択肢2,選択肢3,選択肢4,選択肢5
1,質問文の例,選択肢A,選択肢B,選択肢C,,
```

### Responses (CSV)
```csv
回答者ID,タイムスタンプ,問題番号,質問文,選択した回答,理由
12345678,2025-01-15 10:30:45.123,1,質問文の例,選択肢A,選択した理由...
```

### Action Logs (CSV)
```csv
タイムスタンプ,アクション種別,詳細情報
2025-01-15 10:30:45.123,選択肢選択,問題1: 選択肢A
2025-01-15 10:30:50.456,理由入力開始,問題1
2025-01-15 10:31:20.789,理由入力内容,問題1: 選択した理由は...
```

### JSON Format
```json
{
  "responses": [
    {
      "respondent_id": "12345678",
      "timestamp": "2025-01-15 10:30:45.123",
      "question_num": 1,
      "question_text": "質問文の例",
      "selected_choice": "選択肢A",
      "reason": "選択した理由..."
    }
  ],
  "export_date": "2025-01-15 10:35:00.000",
  "total_responses": 1
}
```

## Technical Stack

- **Language**: Java 11
- **Build Tool**: Maven
- **GUI Framework**: Java Swing
- **Look and Feel**: [FlatLaf](https://www.formdev.com/flatlaf/) 3.2.5
- **JSON Library**: [Gson](https://github.com/google/gson) 2.10.1

## Architecture

The application follows a Model-View-Utility pattern:

- **Model Layer**: Data structures (Question, Response, Config)
- **Utility Layer**: Business logic (ConfigManager, ActionLogger, FileUtils)
- **UI Layer**: User interface components (Swing windows)

Key design principles:
- Centralized constants management (45+ constants in `Constants.java`)
- Clean separation of concerns
- Comprehensive error handling
- Extensive Javadoc documentation (95% coverage)

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines and coding standards.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with [FlatLaf](https://www.formdev.com/flatlaf/) for modern UI
- JSON processing by [Gson](https://github.com/google/gson)
