package form.database;

import form.model.Question;
import form.model.Response;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Database Service and DAO Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseServiceTest {
    private static final String TEST_DB_PATH = "target/test_survey.db";
    private static DatabaseService dbService;
    private static QuestionDAO questionDAO;
    private static ResponseDAO responseDAO;
    private static ActionLogDAO actionLogDAO;

    @BeforeAll
    static void setUp() throws SQLException {
        // テスト用DBファイルを削除
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }

        dbService = new DatabaseService(TEST_DB_PATH);
        questionDAO = new QuestionDAO(dbService);
        responseDAO = new ResponseDAO(dbService);
        actionLogDAO = new ActionLogDAO(dbService);

        // スキーマを初期化
        dbService.initializeSchema();
    }

    @AfterAll
    static void tearDown() {
        dbService.close();
    }

    @Test
    @Order(1)
    @DisplayName("Should initialize database schema")
    void shouldInitializeDatabaseSchema() throws SQLException {
        assertNotNull(dbService.getConnection());
        assertFalse(dbService.getConnection().isClosed());
    }

    @Test
    @Order(2)
    @DisplayName("Should save and retrieve questions")
    void shouldSaveAndRetrieveQuestions() throws SQLException {
        var question = new Question(
            "What is your favorite color?",
            List.of("Red", "Blue", "Green", "Yellow")
        );

        long id = questionDAO.save(question);
        assertTrue(id > 0);

        Question retrieved = questionDAO.findById(id);
        assertNotNull(retrieved);
        assertEquals("What is your favorite color?", retrieved.text());
        assertEquals(4, retrieved.choices().size());
        assertEquals("Red", retrieved.choices().get(0));
    }

    @Test
    @Order(3)
    @DisplayName("Should save and retrieve responses")
    void shouldSaveAndRetrieveResponses() throws SQLException {
        var response = new Response(
            "user123",
            "2025-12-15 10:30:00",
            1,
            "What is your favorite color?",
            List.of("Red", "Blue"),
            "I like both warm and cool colors",
            "A,B"
        );

        long id = responseDAO.save(response);
        assertTrue(id > 0);

        List<Response> responses = responseDAO.findByRespondentId("user123");
        assertEquals(1, responses.size());

        Response retrieved = responses.get(0);
        assertEquals("user123", retrieved.respondentId());
        assertEquals(1, retrieved.questionNum());
        assertEquals(2, retrieved.selectedChoices().size());
        assertTrue(retrieved.selectedChoices().contains("Red"));
        assertTrue(retrieved.selectedChoices().contains("Blue"));
    }

    @Test
    @Order(4)
    @DisplayName("Should save and retrieve action logs")
    void shouldSaveAndRetrieveActionLogs() throws SQLException {
        var log = new ActionLogDAO.ActionLog(
            "user123",
            "2025-12-15 10:30:00.123",
            "CHOICE_SELECTED",
            "Selected choice: Red"
        );

        long id = actionLogDAO.save(log);
        assertTrue(id > 0);

        List<ActionLogDAO.ActionLog> logs = actionLogDAO.findByRespondentId("user123");
        assertTrue(logs.size() > 0);

        ActionLogDAO.ActionLog retrieved = logs.get(0);
        assertEquals("user123", retrieved.respondentId());
        assertEquals("CHOICE_SELECTED", retrieved.actionType());
        assertEquals("Selected choice: Red", retrieved.details());
    }

    @Test
    @Order(5)
    @DisplayName("Should retrieve all questions")
    void shouldRetrieveAllQuestions() throws SQLException {
        List<Question> questions = questionDAO.findAll();
        assertTrue(questions.size() > 0);
    }

    @Test
    @Order(6)
    @DisplayName("Should retrieve all responses")
    void shouldRetrieveAllResponses() throws SQLException {
        List<Response> responses = responseDAO.findAll();
        assertTrue(responses.size() > 0);
    }

    @Test
    @Order(7)
    @DisplayName("Should retrieve all action logs")
    void shouldRetrieveAllActionLogs() throws SQLException {
        List<ActionLogDAO.ActionLog> logs = actionLogDAO.findAll();
        assertTrue(logs.size() > 0);
    }

    @Test
    @Order(8)
    @DisplayName("Should handle multiple questions with same text")
    void shouldHandleMultipleQuestionsWithSameText() throws SQLException {
        var q1 = new Question("Test question", List.of("A", "B"));
        var q2 = new Question("Test question", List.of("C", "D"));

        long id1 = questionDAO.save(q1);
        long id2 = questionDAO.save(q2);

        assertNotEquals(id1, id2);

        Question retrieved1 = questionDAO.findById(id1);
        Question retrieved2 = questionDAO.findById(id2);

        assertEquals(2, retrieved1.choices().size());
        assertEquals(2, retrieved2.choices().size());
        assertEquals("A", retrieved1.choices().get(0));
        assertEquals("C", retrieved2.choices().get(0));
    }

    @Test
    @Order(9)
    @DisplayName("Should handle responses with empty selected choices")
    void shouldHandleResponsesWithEmptySelectedChoices() throws SQLException {
        var response = new Response(
            "user456",
            "2025-12-15 11:00:00",
            2,
            "Test question",
            List.of(),
            "No selection",
            ""
        );

        long id = responseDAO.save(response);
        assertTrue(id > 0);

        List<Response> responses = responseDAO.findByRespondentId("user456");
        assertTrue(responses.size() > 0);

        Response retrieved = responses.get(0);
        assertNotNull(retrieved.selectedChoices());
        assertTrue(retrieved.selectedChoices().isEmpty());
    }

    @Test
    @Order(10)
    @DisplayName("Should delete questions")
    void shouldDeleteQuestions() throws SQLException {
        var question = new Question("To be deleted", List.of("A", "B"));
        long id = questionDAO.save(question);

        Question retrieved = questionDAO.findById(id);
        assertNotNull(retrieved);

        questionDAO.delete(id);

        Question deleted = questionDAO.findById(id);
        assertNull(deleted);
    }
}
