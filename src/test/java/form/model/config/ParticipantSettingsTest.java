package form.model.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ParticipantSettings Record Tests")
class ParticipantSettingsTest {

    @Test
    @DisplayName("Should create ParticipantSettings with all parameters")
    void shouldCreateParticipantSettingsWithAllParameters() {
        var settings = new ParticipantSettings(
            "John Doe",
            "user123",
            true
        );

        assertEquals("John Doe", settings.participantName());
        assertEquals("user123", settings.participantId());
        assertTrue(settings.useParticipantInfo());
    }

    @Test
    @DisplayName("Should create default ParticipantSettings")
    void shouldCreateDefaultParticipantSettings() {
        var settings = ParticipantSettings.createDefault();

        assertNull(settings.participantName());
        assertNull(settings.participantId());
        assertTrue(settings.useParticipantInfo());
    }

    @Test
    @DisplayName("Should allow null participantName")
    void shouldAllowNullParticipantName() {
        var settings = new ParticipantSettings(null, "user123", true);

        assertNull(settings.participantName());
    }

    @Test
    @DisplayName("Should allow null participantId")
    void shouldAllowNullParticipantId() {
        var settings = new ParticipantSettings("John Doe", null, false);

        assertNull(settings.participantId());
    }

    @Test
    @DisplayName("Should allow useParticipantInfo to be false")
    void shouldAllowUseParticipantInfoToBeFalse() {
        var settings = new ParticipantSettings("John Doe", "user123", false);

        assertFalse(settings.useParticipantInfo());
    }

    @Test
    @DisplayName("Should implement equals() correctly")
    void shouldImplementEqualsCorrectly() {
        var s1 = new ParticipantSettings("John", "123", true);
        var s2 = new ParticipantSettings("John", "123", true);
        var s3 = new ParticipantSettings("Jane", "123", true);

        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
    }

    @Test
    @DisplayName("Should implement hashCode() correctly")
    void shouldImplementHashCodeCorrectly() {
        var s1 = new ParticipantSettings("John", "123", true);
        var s2 = new ParticipantSettings("John", "123", true);

        assertEquals(s1.hashCode(), s2.hashCode());
    }
}
