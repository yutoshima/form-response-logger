package form.model.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileSettings Record Tests")
class FileSettingsTest {

    @Test
    @DisplayName("Should create FileSettings with all parameters")
    void shouldCreateFileSettingsWithAllParameters() {
        var settings = new FileSettings(
            "data/questions",
            "questions.csv",
            "data/logs",
            "log_%s.csv",
            "data/responses",
            "response_%s.csv",
            1,
            1
        );

        assertEquals("data/questions", settings.questionsDirectory());
        assertEquals("questions.csv", settings.questionsFile());
        assertEquals("data/logs", settings.logDirectory());
        assertEquals("log_%s.csv", settings.logNameFormat());
        assertEquals("data/responses", settings.responseDirectory());
        assertEquals("response_%s.csv", settings.responseNameFormat());
        assertEquals(1, settings.logSequence());
        assertEquals(1, settings.responseSequence());
    }

    @Test
    @DisplayName("Should create default FileSettings")
    void shouldCreateDefaultFileSettings() {
        var settings = FileSettings.createDefault();

        assertEquals("data/questions", settings.questionsDirectory());
        assertNull(settings.questionsFile());
        assertEquals("data/logs", settings.logDirectory());
        assertNull(settings.logNameFormat());
        assertEquals("data/responses", settings.responseDirectory());
        assertNull(settings.responseNameFormat());
        assertEquals(1, settings.logSequence());
        assertEquals(1, settings.responseSequence());
    }

    @Test
    @DisplayName("Should throw NullPointerException when questionsDirectory is null")
    void shouldThrowNullPointerExceptionWhenQuestionsDirectoryIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new FileSettings(null, "questions.csv", "data/logs", null, "data/responses", null, 1, 1);
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when logDirectory is null")
    void shouldThrowNullPointerExceptionWhenLogDirectoryIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new FileSettings("data/questions", "questions.csv", null, null, "data/responses", null, 1, 1);
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when responseDirectory is null")
    void shouldThrowNullPointerExceptionWhenResponseDirectoryIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new FileSettings("data/questions", "questions.csv", "data/logs", null, null, null, 1, 1);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when logSequence is negative")
    void shouldThrowIllegalArgumentExceptionWhenLogSequenceIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FileSettings("data/questions", null, "data/logs", null, "data/responses", null, -1, 1);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when responseSequence is negative")
    void shouldThrowIllegalArgumentExceptionWhenResponseSequenceIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FileSettings("data/questions", null, "data/logs", null, "data/responses", null, 1, -1);
        });
    }

    @Test
    @DisplayName("Should allow logSequence and responseSequence to be zero")
    void shouldAllowLogSequenceAndResponseSequenceToBeZero() {
        var settings = new FileSettings(
            "data/questions",
            null,
            "data/logs",
            null,
            "data/responses",
            null,
            0,
            0
        );

        assertEquals(0, settings.logSequence());
        assertEquals(0, settings.responseSequence());
    }

    @Test
    @DisplayName("Should implement equals() correctly")
    void shouldImplementEqualsCorrectly() {
        var s1 = new FileSettings("data/questions", null, "data/logs", null, "data/responses", null, 1, 1);
        var s2 = new FileSettings("data/questions", null, "data/logs", null, "data/responses", null, 1, 1);
        var s3 = new FileSettings("different", null, "data/logs", null, "data/responses", null, 1, 1);

        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
    }

    @Test
    @DisplayName("Should implement hashCode() correctly")
    void shouldImplementHashCodeCorrectly() {
        var s1 = new FileSettings("data/questions", null, "data/logs", null, "data/responses", null, 1, 1);
        var s2 = new FileSettings("data/questions", null, "data/logs", null, "data/responses", null, 1, 1);

        assertEquals(s1.hashCode(), s2.hashCode());
    }
}
