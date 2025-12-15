package form.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Response Record Tests")
class ResponseTest {

    @Test
    @DisplayName("Should create Response with all parameters")
    void shouldCreateResponseWithAllParameters() {
        var response = new Response(
            "user123",
            "2025-12-15 10:30:00",
            1,
            "What is your favorite color?",
            List.of("Red", "Blue"),
            "I like both colors",
            "A,B"
        );

        assertEquals("user123", response.respondentId());
        assertEquals("2025-12-15 10:30:00", response.timestamp());
        assertEquals(1, response.questionNum());
        assertEquals("What is your favorite color?", response.questionText());
        assertEquals(2, response.selectedChoices().size());
        assertEquals("I like both colors", response.reason());
        assertEquals("A,B", response.choiceCombination());
    }

    @Test
    @DisplayName("Should create Response with 6 parameters (backward compatibility)")
    void shouldCreateResponseWithSixParameters() {
        var response = new Response(
            "user123",
            "2025-12-15 10:30:00",
            1,
            "Test question",
            List.of("Choice A"),
            "Test reason"
        );

        assertEquals("user123", response.respondentId());
        assertEquals("", response.choiceCombination());
    }

    @Test
    @DisplayName("Should create Response with empty choices when null is provided")
    void shouldCreateResponseWithEmptyChoicesWhenNull() {
        var response = new Response(
            "user123",
            "2025-12-15 10:30:00",
            1,
            "Test",
            null,
            "Reason",
            "A"
        );

        assertNotNull(response.selectedChoices());
        assertTrue(response.selectedChoices().isEmpty());
    }

    @Test
    @DisplayName("Should create Response with empty choiceCombination when null is provided")
    void shouldCreateResponseWithEmptyChoiceCombinationWhenNull() {
        var response = new Response(
            "user123",
            "2025-12-15 10:30:00",
            1,
            "Test",
            List.of("A"),
            "Reason",
            null
        );

        assertEquals("", response.choiceCombination());
    }

    @Test
    @DisplayName("Should create defensive copy of selectedChoices list")
    void shouldCreateDefensiveCopyOfSelectedChoicesList() {
        var originalChoices = new ArrayList<>(List.of("A", "B"));
        var response = new Response(
            "user123",
            "2025-12-15 10:30:00",
            1,
            "Test",
            originalChoices,
            "Reason",
            "A,B"
        );

        // Modify original list
        originalChoices.add("C");

        // Response's choices should remain unchanged
        assertEquals(2, response.selectedChoices().size());
        assertFalse(response.selectedChoices().contains("C"));
    }

    @Test
    @DisplayName("Should make selectedChoices list immutable")
    void shouldMakeSelectedChoicesListImmutable() {
        var response = new Response(
            "user123",
            "2025-12-15 10:30:00",
            1,
            "Test",
            List.of("A", "B"),
            "Reason",
            "A,B"
        );

        assertThrows(UnsupportedOperationException.class, () -> {
            response.selectedChoices().add("C");
        });
    }

    @Test
    @DisplayName("Should create Response using default constructor")
    void shouldCreateResponseUsingDefaultConstructor() {
        var response = new Response();

        assertEquals("", response.respondentId());
        assertEquals("", response.timestamp());
        assertEquals(0, response.questionNum());
        assertEquals("", response.questionText());
        assertNotNull(response.selectedChoices());
        assertTrue(response.selectedChoices().isEmpty());
        assertEquals("", response.reason());
        assertEquals("", response.choiceCombination());
    }

    @Test
    @DisplayName("Should return respondentId using getter method")
    void shouldReturnRespondentIdUsingGetterMethod() {
        var response = new Response("user123", "", 0, "", List.of(), "", "");

        assertEquals("user123", response.getRespondentId());
        assertEquals(response.respondentId(), response.getRespondentId());
    }

    @Test
    @DisplayName("Should return timestamp using getter method")
    void shouldReturnTimestampUsingGetterMethod() {
        var response = new Response("", "2025-12-15 10:30:00", 0, "", List.of(), "", "");

        assertEquals("2025-12-15 10:30:00", response.getTimestamp());
        assertEquals(response.timestamp(), response.getTimestamp());
    }

    @Test
    @DisplayName("Should return questionNum using getter method")
    void shouldReturnQuestionNumUsingGetterMethod() {
        var response = new Response("", "", 5, "", List.of(), "", "");

        assertEquals(5, response.getQuestionNum());
        assertEquals(response.questionNum(), response.getQuestionNum());
    }

    @Test
    @DisplayName("Should return questionText using getter method")
    void shouldReturnQuestionTextUsingGetterMethod() {
        var response = new Response("", "", 0, "What is your name?", List.of(), "", "");

        assertEquals("What is your name?", response.getQuestionText());
        assertEquals(response.questionText(), response.getQuestionText());
    }

    @Test
    @DisplayName("Should return selectedChoices using getter method")
    void shouldReturnSelectedChoicesUsingGetterMethod() {
        var choices = List.of("A", "B", "C");
        var response = new Response("", "", 0, "", choices, "", "");

        assertEquals(choices, response.getSelectedChoices());
        assertEquals(response.selectedChoices(), response.getSelectedChoices());
    }

    @Test
    @DisplayName("Should return reason using getter method")
    void shouldReturnReasonUsingGetterMethod() {
        var response = new Response("", "", 0, "", List.of(), "My reason", "");

        assertEquals("My reason", response.getReason());
        assertEquals(response.reason(), response.getReason());
    }

    @Test
    @DisplayName("Should return choiceCombination using getter method")
    void shouldReturnChoiceCombinationUsingGetterMethod() {
        var response = new Response("", "", 0, "", List.of(), "", "A,C,D");

        assertEquals("A,C,D", response.getChoiceCombination());
        assertEquals(response.choiceCombination(), response.getChoiceCombination());
    }

    @Test
    @DisplayName("Should return joined selectedChoices string")
    void shouldReturnJoinedSelectedChoicesString() {
        var response = new Response(
            "",
            "",
            0,
            "",
            List.of("Choice A", "Choice B", "Choice C"),
            "",
            ""
        );

        assertEquals("Choice A; Choice B; Choice C", response.getSelectedChoice());
    }

    @Test
    @DisplayName("Should return empty string when selectedChoices is empty")
    void shouldReturnEmptyStringWhenSelectedChoicesIsEmpty() {
        var response = new Response("", "", 0, "", List.of(), "", "");

        assertEquals("", response.getSelectedChoice());
    }

    @Test
    @DisplayName("Should return empty string when selectedChoices is null")
    void shouldReturnEmptyStringWhenSelectedChoicesIsNull() {
        var response = new Response("", "", 0, "", null, "", "");

        assertEquals("", response.getSelectedChoice());
    }

    @Test
    @DisplayName("Should implement equals() correctly")
    void shouldImplementEqualsCorrectly() {
        var r1 = new Response("user123", "2025-12-15", 1, "Q1", List.of("A"), "R1", "A");
        var r2 = new Response("user123", "2025-12-15", 1, "Q1", List.of("A"), "R1", "A");
        var r3 = new Response("user456", "2025-12-15", 1, "Q1", List.of("A"), "R1", "A");

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
    }

    @Test
    @DisplayName("Should implement hashCode() correctly")
    void shouldImplementHashCodeCorrectly() {
        var r1 = new Response("user123", "2025-12-15", 1, "Q1", List.of("A"), "R1", "A");
        var r2 = new Response("user123", "2025-12-15", 1, "Q1", List.of("A"), "R1", "A");

        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString() correctly")
    void shouldImplementToStringCorrectly() {
        var response = new Response("user123", "2025-12-15", 1, "Q1", List.of("A"), "R1", "A");
        var toString = response.toString();

        assertTrue(toString.contains("user123"));
        assertTrue(toString.contains("Response"));
    }
}
