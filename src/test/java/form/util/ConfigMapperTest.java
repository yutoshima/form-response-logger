package form.util;

import form.model.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfigMapper Utility Tests")
class ConfigMapperTest {

    @Test
    @DisplayName("Should create Config from Map with default values")
    void shouldCreateConfigFromMapWithDefaultValues() {
        Map<String, Object> map = new HashMap<>();

        Config config = ConfigMapper.fromMap(map);

        assertNotNull(config);
        assertNotNull(config.fileSettings());
        assertNotNull(config.participantSettings());
        assertNotNull(config.uiSettings());
        assertNotNull(config.behaviorSettings());
        assertNotNull(config.recordingSettings());
        assertNotNull(config.buttonLabels());
        assertNotNull(config.windowTitles());
        assertNotNull(config.logActionNames());
    }

    @Test
    @DisplayName("Should create Config from Map with custom values")
    void shouldCreateConfigFromMapWithCustomValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("questions_directory", "custom/questions");
        map.put("log_directory", "custom/logs");
        map.put("response_directory", "custom/responses");
        map.put("participant_name", "John Doe");
        map.put("use_participant_info", false);
        map.put("appearance_mode", "Dark");
        map.put("max_selectable_choices", 3);

        Config config = ConfigMapper.fromMap(map);

        assertEquals("custom/questions", config.fileSettings().questionsDirectory());
        assertEquals("custom/logs", config.fileSettings().logDirectory());
        assertEquals("custom/responses", config.fileSettings().responseDirectory());
        assertEquals("John Doe", config.participantSettings().participantName());
        assertFalse(config.participantSettings().useParticipantInfo());
        assertEquals("Dark", config.uiSettings().appearanceMode());
        assertEquals(3, config.behaviorSettings().maxSelectableChoices());
    }

    @Test
    @DisplayName("Should convert Config to Map")
    void shouldConvertConfigToMap() {
        Map<String, Object> originalMap = new HashMap<>();
        originalMap.put("questions_directory", "test/questions");
        originalMap.put("log_directory", "test/logs");
        originalMap.put("response_directory", "test/responses");
        originalMap.put("participant_name", "Test User");

        Config config = ConfigMapper.fromMap(originalMap);
        Map<String, Object> resultMap = ConfigMapper.toMap(config);

        assertNotNull(resultMap);
        assertEquals("test/questions", resultMap.get("questions_directory"));
        assertEquals("test/logs", resultMap.get("log_directory"));
        assertEquals("test/responses", resultMap.get("response_directory"));
        assertEquals("Test User", resultMap.get("participant_name"));
    }

    @Test
    @DisplayName("Should handle round-trip conversion Config -> Map -> Config")
    void shouldHandleRoundTripConversion() {
        Map<String, Object> originalMap = new HashMap<>();
        originalMap.put("questions_directory", "data/questions");
        originalMap.put("log_directory", "data/logs");
        originalMap.put("response_directory", "data/responses");
        originalMap.put("max_selectable_choices", 5);
        originalMap.put("randomize_choices", true);
        originalMap.put("use_participant_info", true);

        Config config1 = ConfigMapper.fromMap(originalMap);
        Map<String, Object> intermediateMap = ConfigMapper.toMap(config1);
        Config config2 = ConfigMapper.fromMap(intermediateMap);

        // Verify that the two configs have the same values
        assertEquals(config1.fileSettings().questionsDirectory(),
                     config2.fileSettings().questionsDirectory());
        assertEquals(config1.fileSettings().logDirectory(),
                     config2.fileSettings().logDirectory());
        assertEquals(config1.behaviorSettings().maxSelectableChoices(),
                     config2.behaviorSettings().maxSelectableChoices());
        assertEquals(config1.behaviorSettings().randomizeChoices(),
                     config2.behaviorSettings().randomizeChoices());
        assertEquals(config1.participantSettings().useParticipantInfo(),
                     config2.participantSettings().useParticipantInfo());
    }

    @Test
    @DisplayName("Should handle null values in Map gracefully")
    void shouldHandleNullValuesInMapGracefully() {
        Map<String, Object> map = new HashMap<>();
        map.put("questions_directory", null);
        map.put("participant_name", null);
        map.put("questions_file", null);

        // This might throw an exception due to validation in FileSettings
        // or handle null gracefully depending on implementation
        assertDoesNotThrow(() -> {
            Config config = ConfigMapper.fromMap(map);
            assertNull(config.participantSettings().participantName());
        });
    }

    @Test
    @DisplayName("Should preserve boolean values in conversion")
    void shouldPreserveBooleanValuesInConversion() {
        Map<String, Object> map = new HashMap<>();
        map.put("questions_directory", "data/q");
        map.put("log_directory", "data/l");
        map.put("response_directory", "data/r");
        map.put("randomize_choices", true);
        map.put("enable_prev_button", false);
        map.put("use_participant_info", true);
        map.put("use_html_rendering", false);

        Config config = ConfigMapper.fromMap(map);

        assertTrue(config.behaviorSettings().randomizeChoices());
        assertFalse(config.behaviorSettings().enablePrevButton());
        assertTrue(config.participantSettings().useParticipantInfo());
        assertFalse(config.uiSettings().useHtmlRendering());
    }

    @Test
    @DisplayName("Should preserve integer values in conversion")
    void shouldPreserveIntegerValuesInConversion() {
        Map<String, Object> map = new HashMap<>();
        map.put("questions_directory", "data/q");
        map.put("log_directory", "data/l");
        map.put("response_directory", "data/r");
        map.put("max_selectable_choices", 7);
        map.put("log_sequence", 42);
        map.put("response_sequence", 99);
        map.put("choice_columns", 3);

        Config config = ConfigMapper.fromMap(map);

        assertEquals(7, config.behaviorSettings().maxSelectableChoices());
        assertEquals(42, config.fileSettings().logSequence());
        assertEquals(99, config.fileSettings().responseSequence());
        assertEquals(3, config.behaviorSettings().choiceColumns());
    }
}
