package se.lexicon.g58todoapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.lexicon.g58todoapp.dto.TodoDto;
import se.lexicon.g58todoapp.service.TodoService;

@RequestMapping("api/todos")
@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }


    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody TodoDto todoDto){

        todoService.create(todoDto);

        return ResponseEntity.status(201).build();

    }


}
