package se.lexicon.g58todoapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.lexicon.g58todoapp.entity.Person;
import se.lexicon.g58todoapp.entity.Todo;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // find tasks assigned to a specific Person
    List<Todo> findByAssignedTo(Person assignedTo);

    // Count all tasks assigned to a person
    int countByAssignedTo(Person assignedTo);

    // Find completed tasks assigned to a specific person
    List<Todo> findByAssignedToAndCompletedTrue(Person assignedTo);

    // Find todos by title keyword (case-insensitive contains)
    List<Todo> findByTitleContainsIgnoreCase(String title);

    // Find todos by completed status
    List<Todo> findByCompleted(Boolean completed);

    // Find todos between two due dates
    List<Todo> findByDueDateBetween(LocalDateTime dueDateAfter, LocalDateTime dueDateBefore);

    // Find todo due before a specific date and not completed
    List<Todo> findByDueDateBeforeAndCompletedFalse(LocalDateTime dueDateBefore);

    // Find unfinished and overdue task
    @Query("SELECT todo FROM Todo todo WHERE todo.completed = FALSE AND todo.dueDate < CURRENT_TIMESTAMP")
    List<Todo> findByCompletedFalseAndOverdue();

    // Find tasks that are not assigned to anyone
    List<Todo> findByAssignedToNull();

    // Find all with no due date
    List<Todo> findByDueDateIsNull();
}