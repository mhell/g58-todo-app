package se.lexicon.g58todoapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.lexicon.g58todoapp.entity.Todo;
import se.lexicon.g58todoapp.repo.TodoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/todoitems")
@RestController
public class TodoController {

    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // find all todos
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Todo> getTodoItems() {
        return todoRepository.findAll();
    }

    // find one todo
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Todo getTodoById(@PathVariable Long id){
        return todoRepository.findById(id).orElseThrow();
    }

    // find todos assigned to a person
    @GetMapping(params = "assignee")
    @ResponseStatus(HttpStatus.OK)
    public List<Todo> findByAssignedTo(@RequestParam Long assignee) {
        return todoRepository.findByAssignedTo_Id(assignee);
    }

    // find todos between two due dates
    @GetMapping(params = {"before", "after"})
    @ResponseStatus(HttpStatus.OK)
    public List<Todo> findByDueDateBetween(@RequestParam LocalDateTime before, @RequestParam LocalDateTime after) {
        return todoRepository.findByDueDateBetween(before, after);
    }

    // create new todo
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTodo(@RequestBody Todo todo){
        todoRepository.save(todo);
    }

    // TODO: Use DTO:s
    // update a todo
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTodo(@PathVariable Long id, @RequestBody Todo todo){
        Optional<Todo> detached = todoRepository.findById(id);
        if (detached.isEmpty()) {
            throw new IllegalArgumentException();
        }
        todoRepository.save(todo);
    }

    // delete todo
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable Long id){
        todoRepository.deleteById(id);
    }
}
