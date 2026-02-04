package se.lexicon.g58todoapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lexicon.g58todoapp.dto.TodoDto;
import se.lexicon.g58todoapp.service.TodoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/todoitems")
@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // create new todo
    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody TodoDto todo){
        TodoDto todoDto = todoService.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(todoDto);
    }

    // find all todos
    @GetMapping
    public ResponseEntity<List<TodoDto>> getTodoItems() {
        var result =  todoService.findAll();
        return ResponseEntity.ok(result);
    }

    // find one todo
    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodoById(@PathVariable Long id){
        Optional<TodoDto> optional = todoService.findById(id);
        if(optional.isPresent()) {
            return ResponseEntity.ok(optional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // find todos assigned to a person
    @GetMapping(params = "assignee")
    public ResponseEntity<List<TodoDto>> findByAssignedTo(@RequestParam Long assignee) {
        List<TodoDto> result = todoService.findByAssignedTo_Id(assignee);
        return ResponseEntity.ok(result);
    }

    // find todos between two due dates
    @GetMapping(params = {"before", "after"})
    public ResponseEntity<List<TodoDto>> findByDueDateBetween(@RequestParam LocalDateTime before, @RequestParam LocalDateTime after) {
        List<TodoDto> result = todoService.findByDueDateBetween(before, after);
        return ResponseEntity.ok(result);
    }

    // update a todo
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<TodoDto>  updateTodo(@PathVariable Long id, @RequestBody TodoDto todoDto){
        if(todoDto.id().equals(id)) {
            TodoDto updated = todoService.updateTodo(todoDto);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // delete todo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id){
        return todoService.deleteTodo(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
