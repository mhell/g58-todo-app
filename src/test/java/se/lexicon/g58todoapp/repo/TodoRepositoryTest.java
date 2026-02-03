package se.lexicon.g58todoapp.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.lexicon.g58todoapp.entity.Attachment;
import se.lexicon.g58todoapp.entity.Person;
import se.lexicon.g58todoapp.entity.Todo;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final String TEST_TITLE = "Test title";
    private final String TEST_DESC = "Test description";
    private final LocalDateTime TEST_TIME = LocalDateTime.now();
    private Person testPerson;

    @BeforeEach
    void setUp() {
        testPerson = new Person("Test Person", "test@example.com");
    }

    @Test
    @DisplayName("Save Todo should persist the Todo and return it with generated ID")
    void save_todo_shouldPersistTodo() {
        // Arrange
        Todo newTodo = new Todo(TEST_TITLE, TEST_DESC, TEST_TIME.plusDays(1));
        // Act
        Todo saved = todoRepository.save(newTodo);
        // Assert
        assertNotNull(saved.getId());
        assertEquals(TEST_TITLE, saved.getTitle());
        assertEquals(TEST_DESC, saved.getDescription());
        assertFalse(saved.getCompleted());
        assertEquals(TEST_TIME.plusDays(1).withNano(0), saved.getDueDate().withNano(0));
    }
    
    @Test
    @DisplayName("Test cascade persist")
    void getAttachment_afterSave_returnAttachment() {
        // Arrange
        String attachmentFileName = "fileName";
        Attachment attachment = new Attachment(attachmentFileName, "fileType", "test".getBytes());
        Todo todo = new Todo(TEST_TITLE, TEST_DESC);
        todo.addAttachment(attachment);
        todoRepository.save(todo);
        // Act
        Attachment savedAttachment = entityManager.find(Attachment.class, attachment.getId());
        // Assert
        assertNotNull(savedAttachment);
        assertEquals(attachmentFileName, savedAttachment.getFileName());
    }

    @Test
    @DisplayName("Test cascade remove")
    void getAttachment_afterRemove_expectNull() {
        // Arrange
        Attachment attachment = new Attachment("fileName", "fileType", "test".getBytes());
        Todo todo = new Todo(TEST_TITLE, TEST_DESC);
        todo.addAttachment(attachment);
        todoRepository.save(todo);
        todoRepository.delete(todo);
        // Act
        Attachment savedAttachment = entityManager.find(Attachment.class, attachment.getId());
        // Assert
        assertNull(savedAttachment);
    }

    @Test
    @DisplayName("Test orphan removal")
    void getAttachment_afterRemoved_ExpectNull() {
        // Arrange
        Attachment attachment = new Attachment("fileName", "fileType", "test".getBytes());
        Todo todo = new Todo(TEST_TITLE, TEST_DESC);
        todo.addAttachment(attachment);
        Todo savedTodo = todoRepository.save(todo);
        // Act
        savedTodo.removeAttachment(attachment);
        todoRepository.flush();
        Attachment foundAttachment = entityManager.find(Attachment.class, attachment.getId());
        // Assert
        assertNull(foundAttachment);
    }

    @Test
    @DisplayName("Find Todos containing case-insensitive title substring should return matching Todos")
    void findByTitleContainingIgnoreCase_ShouldReturnMatchingTodos() {
        // Arrange
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC));
        String searchString = "TITLE";
        // Act
        List<Todo> retrievedTodos = todoRepository.findByTitleContainsIgnoreCase(searchString);
        // Assert
        assertEquals(1, retrievedTodos.size());
    }

    @Test
    @DisplayName("Find Todos by Person should return that person's Todos")
    void findByAssignedTo_ShouldReturnPersonsTodos() {
        // Arrange
        entityManager.persist(testPerson);
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, testPerson));
        // Act
        List<Todo> retrievedTodos = todoRepository.findByAssignedTo_Id(testPerson.getId());
        // Assert
        assertEquals(1, retrievedTodos.size());
        assertEquals(TEST_TITLE, retrievedTodos.getFirst().getTitle());
        assertEquals(TEST_DESC, retrievedTodos.getFirst().getDescription());
    }

    @Test
    @DisplayName("Find Todos by completion status should return completed Todos")
    void findByCompleted_ShouldReturnCompletedTodos() {
        // Arrange
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC));
        Todo savedTodo = todoRepository.save(new Todo(TEST_TITLE, TEST_DESC));
        savedTodo.setCompleted(true);
        // Act
        List<Todo> retrievedTodos = todoRepository.findByCompleted(true);
        // Assert
        assertEquals(1, retrievedTodos.size());
    }

    @Test
    @DisplayName("Find Todos due within a date range should return matching Todos")
    void findByDueDateBetween_ShouldReturnTodosInDateRange() {
        // Arrange
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME));
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME.plusDays(100)));
        // Act
        List<Todo> retrievedTodos = todoRepository.findByDueDateBetween(TEST_TIME.minusDays(1), TEST_TIME.plusDays(1));
        // Assert
        assertEquals(1, retrievedTodos.size());
    }

    @Test
    @DisplayName("Find overdue and incomplete Todos should return matching Todos")
    void findByDueDateBeforeAndCompletedFalse_ShouldReturnOverdueTodos() {
        // Arrange
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME));
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME.minusDays(1)));
        Todo savedTodo = todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME.minusDays(1)));
        savedTodo.setCompleted(true);
        // Act
        List<Todo> retrievedTodos = todoRepository.findByDueDateBeforeAndCompletedFalse(TEST_TIME);
        // Assert
        assertEquals(1, retrievedTodos.size());
    }

    @Test
    @DisplayName("Find unassigned Todos should return Todos with no Person set")
    void findByAssignedToNull_ShouldReturnUnassignedTodos() {
        // Arrange
        entityManager.persist(testPerson);
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, testPerson));
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, null));
        // Act
        List<Todo> retrievedTodos = todoRepository.findByAssignedToNull();
        // Assert
        assertEquals(1, retrievedTodos.size());
        assertNull(retrievedTodos.getFirst().getAssignedTo());
    }

    @Test
    @DisplayName("Find unfinished overdue Todos should return matching Todos")
    void findByCompletedFalseAndOverdue_ShouldReturnUnfinishedOverdueTasks() {
        // Arrange
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME.plusDays(1)));
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME.minusDays(1)));
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, true, TEST_TIME.minusDays(1)));
        // Act
        List<Todo> retrievedTodos = todoRepository.findByCompletedFalseAndOverdue();
        // Assert
        assertEquals(1, retrievedTodos.size());
        assertFalse(retrievedTodos.getFirst().getCompleted());
        assertTrue(retrievedTodos.getFirst().isOverdue());
    }

    @Test
    @DisplayName("Find completed Todos for a Person by ID should return matching Todos")
    void findByAssignedToAndCompletedTrue_ShouldReturnCompletedTasksForPerson() {
        // Arrange
        Person testPerson2 = new Person("Test Person 2", "test2@example.com");
        entityManager.persist(testPerson);
        entityManager.persist(testPerson2);
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, testPerson));
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, testPerson2));
        Todo savedTodo = todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, testPerson2));
        savedTodo.setCompleted(true);
        // Act
        List<Todo> retrievedTodos = todoRepository.findByAssignedTo_IdAndCompletedTrue(testPerson2.getId());
        // Assert
        assertEquals(1, retrievedTodos.size());
        assertTrue(retrievedTodos.getFirst().getCompleted());
        assertEquals(testPerson2.getId(), retrievedTodos.getFirst().getAssignedTo().getId());

    }

    @Test
    @DisplayName("Find Todos with no due date should return matching Todos")
    void findByDueDateIsNull_ShouldReturnTodosWithNoDueDate() {
        // Arrange
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME));
        Todo savedTodo = todoRepository.save(new Todo(TEST_TITLE, TEST_DESC));
        // Act
        List<Todo> retrievedTodos = todoRepository.findByDueDateIsNull();
        // Assert
        assertEquals(1, retrievedTodos.size());
        assertNull(retrievedTodos.getFirst().getDueDate());
        assertEquals(savedTodo, retrievedTodos.getFirst());
    }

    @Test
    @DisplayName("Count Todos for a Person by ID should return correct count")
    void countByAssignedTo_ShouldReturnCorrectCount() {
        // Arrange
        Person testPerson2 = new Person("Test Person 2", "test2@example.com");
        entityManager.persist(testPerson);
        entityManager.persist(testPerson2);
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, testPerson));
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, testPerson2));
        todoRepository.save(new Todo(TEST_TITLE, TEST_DESC, TEST_TIME, testPerson2));
        // Act
        int count = todoRepository.countByAssignedTo_Id(testPerson2.getId());
        // Assert
        assertThat(count).isEqualTo(2);
    }

}