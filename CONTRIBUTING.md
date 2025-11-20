# Contributing to Form Response Logger

Thank you for your interest in contributing to Form Response Logger! This document provides guidelines for contributing to the project.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Development Setup](#development-setup)
3. [Code Style](#code-style)
4. [Project Structure](#project-structure)
5. [Making Changes](#making-changes)
6. [Submitting Pull Requests](#submitting-pull-requests)

## Getting Started

### Prerequisites

- **Java**: OpenJDK 11 or higher
- **Maven**: 3.6 or higher
- **Git**: Latest version
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code (optional but recommended)

### Development Setup

```bash
# Fork and clone the repository
git clone https://github.com/your-username/form-response-logger.git
cd form-response-logger

# Build the project
mvn clean install

# Run the application
mvn exec:java -Dexec.mainClass="com.study.form.SurveyApp"
```

### IDE Configuration

#### IntelliJ IDEA
1. Open the project directory (`File` → `Open`)
2. Maven project will be auto-detected
3. Set up run configuration:
   - Main class: `com.study.form.SurveyApp`

#### Eclipse
1. Import project: `File` → `Import` → `Maven` → `Existing Maven Projects`
2. Select the project directory

## Code Style

### General Principles

1. **Readability First**: Code is read more often than written
2. **DRY Principle**: Don't Repeat Yourself
3. **Single Responsibility**: Each class/method should have one responsibility
4. **Use Constants**: No magic numbers or strings - use `Constants.java`

### Java Naming Conventions

```java
// Classes: PascalCase
public class ConfigManager { }

// Methods: camelCase
public void loadConfig() { }

// Variables: camelCase
private String questionsFile;

// Constants: UPPER_SNAKE_CASE
public static final String CONFIG_FILE = "config.json";

// Packages: lowercase
package com.study.form.util;
```

### Code Formatting

- **Indentation**: 4 spaces
- **Line length**: 100 characters recommended (120 max)
- **Braces**: K&R style (opening brace on same line)
- **Spacing**: Space after keywords, before opening braces

```java
// Good
public void saveConfig() {
    if (config != null) {
        // process
    }
}

// Bad
public void saveConfig()
{
    if(config!=null){
        // process
    }
}
```

### Javadoc

All public classes and methods should have Javadoc comments:

```java
/**
 * Manages application configuration.
 *
 * <p>This class handles loading, saving, and managing the application's
 * configuration settings stored in config.json.</p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public class ConfigManager {

    /**
     * Loads configuration from file.
     *
     * <p>If the file doesn't exist or is invalid, creates default configuration.</p>
     */
    private void loadConfig() {
        // implementation
    }
}
```

## Project Structure

```
src/main/java/com/study/form/
├── SurveyApp.java           # Main entry point
├── Constants.java           # Centralized constants (45+ constants)
├── model/                   # Data models
│   ├── Question.java        # Question and choices
│   ├── Response.java        # Survey response data
│   └── Config.java          # Application settings
├── util/                    # Business logic utilities
│   ├── ConfigManager.java   # Config file management
│   ├── ActionLogger.java    # User action logging
│   └── FileUtils.java       # CSV/JSON I/O
└── ui/                      # User interface (Swing)
    ├── MainWindow.java
    ├── QuestionEditorWindow.java
    ├── SurveyInterfaceWindow.java
    ├── SettingsWindow.java
    └── ParticipantInfoWindow.java
```

### Layer Responsibilities

- **Model**: Data structures only, no business logic
- **Util**: Business logic, file I/O, data processing
- **UI**: User interface, event handling, minimal logic

## Recent Updates

### Latest Features (2025)

- **HTML Support in Questions**: Questions now support HTML tags for rich formatting
  - Bold (`<b>`), italic (`<i>`), underline (`<u>`)
  - Line breaks (`<br>`), paragraphs (`<p>`)
  - CSS styling for custom formatting
  - See `data/questions/html_test.csv` for examples

- **Question Editor Improvements**:
  - Double-click to edit existing questions
  - Edit mode with visual indicators
  - Cancel button to abort editing

- **Improved UI**:
  - Question text now supports vertical scrolling for long content
  - Reduced reason input area from 6 to 4 rows
  - HTML rendering with proper font styling

- **Better Logging**:
  - Detailed console output for debugging
  - Log directory and file creation messages

- **Settings Management**:
  - Fixed question file path saving (split directory and filename)
  - Proper full path handling in settings window

### Known HTML/CSS Support

The question display uses `JEditorPane` with HTML support. Available tags and styles:

```html
<!-- Basic formatting -->
<b>Bold text</b> or <strong>Bold</strong>
<i>Italic text</i> or <em>Italic</em>
<u>Underlined text</u>

<!-- Structure -->
<br> Line break
<p>Paragraph</p>
<h1>Heading 1</h1> through <h6>Heading 6</h6>

<!-- Lists -->
<ul><li>Unordered list item</li></ul>
<ol><li>Ordered list item</li></ol>

<!-- Styling -->
<font color="red">Colored text</font>
<span style="color: blue; font-size: 14pt;">Styled text</span>

<!-- CSS in style tag -->
<html>
  <head>
    <style>
      body { font-family: 'Yu Gothic'; font-size: 16pt; }
      .highlight { background-color: yellow; }
    </style>
  </head>
  <body>
    <p class="highlight">Highlighted paragraph</p>
  </body>
</html>
```

## Making Changes

### Adding New Features

#### 1. Add a New Setting

**Step 1**: Update `Config.java`
```java
private String newSetting;

public String getNewSetting() { return newSetting; }
public void setNewSetting(String value) { newSetting = value; }

// Update toMap() and fromMap() methods
```

**Step 2**: Add constant to `Constants.java` (if needed)
```java
public static final String DEFAULT_NEW_SETTING = "default_value";
```

**Step 3**: Update `SettingsWindow.java` to add UI component

#### 2. Add a New Filename Variable

**Step 1**: Update `ConfigManager.formatFilename()`
```java
filename = filename.replace("{new_variable}", actualValue);
```

**Step 2**: Document in README and settings help text

#### 3. Add a New Log Action

**Step 1**: Add constant to `Constants.java`
```java
public static final String LOG_ACTION_NEW_EVENT = "新しいイベント";
```

**Step 2**: Use `ActionLogger` to record
```java
actionLogger.logAction(Constants.LOG_ACTION_NEW_EVENT, "詳細情報");
```

### Error Handling

Always use specific exception types:

```java
// Good
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

// Bad
try {
    // operation
} catch (Exception e) {
    e.printStackTrace();
}
```

### Null Safety

Always validate parameters:

```java
public String getLogPath(String respondentId) {
    if (respondentId == null) {
        throw new IllegalArgumentException("respondentIdがnullです");
    }
    return formatFilePath(config.getLogDirectory(), config.getLogNameFormat(), respondentId);
}
```

## Submitting Pull Requests

### Before Submitting

1. **Build succeeds**: `mvn clean package`
2. **Code follows style guide**
3. **Javadoc is complete**
4. **No warnings** in IDE
5. **Test manually** if adding UI changes

### Commit Messages

Use clear, descriptive commit messages:

```
Good:
- Add sequence number editing to settings window
- Fix CSV escaping for quotes in responses
- Improve error handling in FileUtils

Bad:
- Update
- Fix bug
- Changes
```

### Pull Request Process

1. **Fork** the repository
2. **Create a branch**: `git checkout -b feature/your-feature-name`
3. **Make changes** following the guidelines above
4. **Commit** with clear messages
5. **Push**: `git push origin feature/your-feature-name`
6. **Open Pull Request** with description of changes

### PR Description Template

```markdown
## Description
Brief description of what this PR does

## Changes
- Change 1
- Change 2

## Testing
How you tested these changes

## Screenshots (if UI changes)
Add screenshots if relevant
```

## Code Quality Guidelines

### Current Metrics (Maintain or Improve)

- Javadoc coverage: 95%+
- Magic numbers: 0 (use Constants.java)
- Code duplication: Minimal
- Average method length: < 20 lines
- Max method length: < 40 lines

### Best Practices

1. **Use Constants**: All UI sizes, colors, messages in `Constants.java`
2. **Extract Methods**: Break long methods into smaller, focused ones
3. **Meaningful Names**: Variables and methods should be self-documenting
4. **Comments**: Explain "why", not "what" (code shows "what")
5. **Resource Management**: Always use try-with-resources for I/O

## Questions?

Feel free to open an issue for:
- Bug reports
- Feature requests
- Questions about contributing
- Suggestions for improvements

Thank you for contributing to Form Response Logger!
