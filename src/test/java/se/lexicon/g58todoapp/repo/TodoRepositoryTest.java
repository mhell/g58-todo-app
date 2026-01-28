package se.lexicon.g58todoapp.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.lexicon.g58todoapp.entity.Attachment;
import se.lexicon.g58todoapp.entity.Person;
import se.lexicon.g58todoapp.entity.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // TODO: More tests

    @Test
    void findByAssignedTo() {
    }

    @Test
    void countByAssignedTo() {
    }

    @Test
    void findByAssignedToAndCompletedTrue() {
    }

    @Test
    void findByTitleContainsIgnoreCase() {
    }

    @Test
    void findByCompleted() {
    }

    @Test
    void findByDueDateBetween() {
    }

    @Test
    void findByDueDateBeforeAndCompletedFalse() {
    }

    @Test
    void findByNonCompletedAndOverdue_exist_returnEntity() {
        // Arrange
        Todo savedTodo = todoRepository.save(new Todo("title", "description", LocalDateTime.now().minusDays(1)));
        // Act
        List<Todo> foundTodos = todoRepository.findByCompletedFalseAndOverdue();
        // Assert
        assertFalse(foundTodos.isEmpty());
        assertEquals(savedTodo.getId(), foundTodos.getFirst().getId());
    }

    @Test
    void findByNonCompletedAndOverdue_noneOverdue_returnNone() {
        // Arrange
        Todo savedTodo = todoRepository.save(new Todo("title", "description", LocalDateTime.now().plusDays(1)));
        // Act
        List<Todo> foundTodos = todoRepository.findByCompletedFalseAndOverdue();
        // Assert
        assertTrue(foundTodos.isEmpty());
    }

    @Test
    void findByNonCompletedAndOverdue_allCompleted_returnNone() {
        // Arrange
        Todo savedTodo = todoRepository.save(new Todo("title", "description", true, LocalDateTime.now().minusDays(1)));
        // Act
        List<Todo> foundTodos = todoRepository.findByCompletedFalseAndOverdue();
        // Assert
        assertTrue(foundTodos.isEmpty());
    }

    @Test
    void findByNonCompletedAndOverdue_allCompletedNoneOverdue_returnNone() {
        // Arrange
        Todo savedTodo = todoRepository.save(new Todo("title", "description", true, LocalDateTime.now().plusDays(1)));
        // Act
        List<Todo> foundTodos = todoRepository.findByCompletedFalseAndOverdue();
        // Assert
        assertTrue(foundTodos.isEmpty());
    }

    @Test
    void findByAssignedToNull_exist_returnEntity() {
        // Arrange
        Todo savedTodo = todoRepository.save(new Todo("title", "description"));
        // Act
        List<Todo> foundTodos = todoRepository.findByAssignedToNull();
        // Assert
        assertFalse(foundTodos.isEmpty());
        assertEquals(savedTodo.getId(), foundTodos.getFirst().getId());
    }

    @Test
    void findByAssignedToNull_dontExist_returnNone() {
        // Arrange
        Person person = new Person("Jesus", "Christ", LocalDate.of(0, 1, 1));
        Todo savedTodo = todoRepository.save(new Todo("title", "description", LocalDateTime.now(), person));
        // Act
        List<Todo> foundTodos = todoRepository.findByAssignedToNull();
        // Assert
        assertTrue(foundTodos.isEmpty());
    }

    @Test
    void findByDueDateIsNull_exist_returnEntity() {
        // Arrange
        Todo savedTodo = todoRepository.save(new Todo("title", "description"));
        // Act
        List<Todo> foundTodos = todoRepository.findByDueDateIsNull();
        // Assert
        assertFalse(foundTodos.isEmpty());
        assertEquals(savedTodo.getId(), foundTodos.getFirst().getId());
    }

    @Test
    void findByDueDateIsNull_dontExist_returnNone() {
        // Arrange
        Todo savedTodo = todoRepository.save(new Todo("title", "description", LocalDateTime.now()));
        // Act
        List<Todo> foundTodos = todoRepository.findByDueDateIsNull();
        // Assert
        assertTrue(foundTodos.isEmpty());
    }

    @Test
    @DisplayName("Test cascade persist")
    void getAttachment_afterSave_returnAttachment() {
        // Arrange
        String attachmentFileName = "fileName";
        Attachment attachment = new Attachment(attachmentFileName, "fileType", "test".getBytes());
        Todo todo = new Todo("title", "description");
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
        Todo todo = new Todo("title", "description");
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
        Todo todo = new Todo("title", "description");
        todo.addAttachment(attachment);
        Todo savedTodo = todoRepository.save(todo);
        // Act
        savedTodo.removeAttachment(attachment);
        todoRepository.flush();
        Attachment foundAttachment = entityManager.find(Attachment.class, attachment.getId());
        // Assert
        assertNull(foundAttachment);
    }
}