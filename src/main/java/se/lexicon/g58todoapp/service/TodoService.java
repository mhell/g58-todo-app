package se.lexicon.g58todoapp.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import se.lexicon.g58todoapp.dto.TodoDto;
import se.lexicon.g58todoapp.entity.Person;
import se.lexicon.g58todoapp.entity.Todo;
import se.lexicon.g58todoapp.exception.PersonNotFoundException;
import se.lexicon.g58todoapp.exception.TodoNotFoundException;
import se.lexicon.g58todoapp.repo.PersonRepository;
import se.lexicon.g58todoapp.repo.TodoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final PersonRepository personRepository;

    public TodoService(TodoRepository todoRepository, PersonRepository personRepository) {
        this.todoRepository = todoRepository;
        this.personRepository = personRepository;
    }

    public TodoDto createTodo(TodoDto todoDto) {
        // Convert dto to entity
        Todo todo = new Todo(todoDto.title(), todoDto.description(), todoDto.completed(), todoDto.dueDate());
        //save entity
        Todo saved = todoRepository.save(todo);
        // convert to dto.
        return convertToDto(saved);
    }

    public List<TodoDto> findAll() {
        return todoRepository.findAll().stream().map(this::convertToDto).toList();
    }

    public Optional<TodoDto> findById(Long id) {
        Optional<Todo> todo = todoRepository.findById(id);
        return todo.map(this::convertToDto);
    }

    public List<TodoDto> findByAssignedTo_Id(Long assignee) {
        List<Todo> todos = todoRepository.findByAssignedTo_Id(assignee);
        return todos.stream().map(this::convertToDto).toList();
    }

    public List<TodoDto> findByDueDateBetween(LocalDateTime before, LocalDateTime after) {
        List<Todo> todos = todoRepository.findByDueDateBetween(before, after);
        return todos.stream().map(this::convertToDto).toList();
    }

    @Transactional
    public TodoDto updateTodo(TodoDto todoDto) {
        Todo todo = todoRepository.findById(todoDto.id()).orElseThrow(TodoNotFoundException::new);
        todo.setTitle(todoDto.title());
        todo.setDescription(todoDto.description());
        todo.setCompleted(todoDto.completed());
        todo.setDueDate(todoDto.dueDate());
        Person assignee = todoDto.assignedToId() == null ? null :
                personRepository.findById(todoDto.assignedToId()).orElseThrow(PersonNotFoundException::new);
        todo.setAssignedTo(assignee);
        return convertToDto(todoRepository.save(todo));
    }

    public boolean deleteTodo(Long id) {
        Optional<Todo> todo = todoRepository.findById(id);
        if (todo.isPresent()) {
            todoRepository.delete(todo.get());
            return true;
        }
        return false;
    }

    private TodoDto convertToDto (Todo todo) {
        return TodoDto.builder().
                id(todo.getId()).
                title(todo.getTitle()).
                description(todo.getDescription()).
                dueDate(todo.getDueDate()).
                completed(todo.getCompleted()).
                createdAt(todo.getCreatedAt()).
                updatedAt(todo.getUpdatedAt()).
                assignedToId(todo.getAssignedTo() != null ? todo.getAssignedTo().getId() : null).
                numberOfAttachments(todo.getAttachments().size()).
                build();
    }
}
