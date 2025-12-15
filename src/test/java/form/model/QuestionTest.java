package form.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Question Record Tests")
class QuestionTest {

    @Test
    @DisplayName("Should create Question with text and choices")
    void shouldCreateQuestionWithTextAndChoices() {
        var choices = List.of("Choice A", "Choice B", "Choice C");
        var question = new Question("What is your favorite color?", choices);

        assertEquals("What is your favorite color?", question.text());
        assertEquals(3, question.choices().size());
        assertEquals("Choice A", question.choices().get(0));
    }

    @Test
    @DisplayName("Should create Question with empty choices when null is provided")
    void shouldCreateQuestionWithEmptyChoicesWhenNull() {
        var question = new Question("Test question", null);

        assertNotNull(question.choices());
        assertTrue(question.choices().isEmpty());
    }

    @Test
    @DisplayName("Should create defensive copy of choices list")
    void shouldCreateDefensiveCopyOfChoicesList() {
        var originalChoices = new ArrayList<>(List.of("A", "B", "C"));
        var question = new Question("Test", originalChoices);

        // Modify original list
        originalChoices.add("D");

        // Question's choices should remain unchanged
        assertEquals(3, question.choices().size());
        assertFalse(question.choices().contains("D"));
    }

    @Test
    @DisplayName("Should make choices list immutable")
    void shouldMakeChoicesListImmutable() {
        var question = new Question("Test", List.of("A", "B"));

        assertThrows(UnsupportedOperationException.class, () -> {
            question.choices().add("C");
        });
    }

    @Test
    @DisplayName("Should create Question using default constructor")
    void shouldCreateQuestionUsingDefaultConstructor() {
        var question = new Question();

        assertEquals("", question.text());
        assertNotNull(question.choices());
        assertTrue(question.choices().isEmpty());
    }

    @Test
    @DisplayName("Should return text using getText() method")
    void shouldReturnTextUsingGetTextMethod() {
        var question = new Question("Sample question", List.of());

        assertEquals("Sample question", question.getText());
        assertEquals(question.text(), question.getText());
    }

    @Test
    @DisplayName("Should return choices using getChoices() method")
    void shouldReturnChoicesUsingGetChoicesMethod() {
        var choices = List.of("A", "B", "C");
        var question = new Question("Test", choices);

        assertEquals(choices, question.getChoices());
        assertEquals(question.choices(), question.getChoices());
    }

    @Test
    @DisplayName("Should create new instance with updated text using setText()")
    void shouldCreateNewInstanceWithUpdatedText() {
        var original = new Question("Original", List.of("A", "B"));
        var updated = original.setText("Updated");

        assertEquals("Original", original.text());
        assertEquals("Updated", updated.text());
        assertEquals(original.choices(), updated.choices());
        assertNotSame(original, updated);
    }

    @Test
    @DisplayName("Should create new instance with updated choices using setChoices()")
    void shouldCreateNewInstanceWithUpdatedChoices() {
        var original = new Question("Test", List.of("A", "B"));
        var newChoices = List.of("X", "Y", "Z");
        var updated = original.setChoices(newChoices);

        assertEquals(original.text(), updated.text());
        assertEquals(2, original.choices().size());
        assertEquals(3, updated.choices().size());
        assertNotSame(original, updated);
    }

    @Test
    @DisplayName("Should implement equals() correctly")
    void shouldImplementEqualsCorrectly() {
        var q1 = new Question("Test", List.of("A", "B"));
        var q2 = new Question("Test", List.of("A", "B"));
        var q3 = new Question("Different", List.of("A", "B"));

        assertEquals(q1, q2);
        assertNotEquals(q1, q3);
    }

    @Test
    @DisplayName("Should implement hashCode() correctly")
    void shouldImplementHashCodeCorrectly() {
        var q1 = new Question("Test", List.of("A", "B"));
        var q2 = new Question("Test", List.of("A", "B"));

        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString() correctly")
    void shouldImplementToStringCorrectly() {
        var question = new Question("Test", List.of("A", "B"));
        var toString = question.toString();

        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("Question"));
    }
}
