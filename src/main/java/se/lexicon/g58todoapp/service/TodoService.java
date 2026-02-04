package se.lexicon.g58todoapp.service;

import org.springframework.stereotype.Service;
import se.lexicon.g58todoapp.dto.TodoDto;
import se.lexicon.g58todoapp.entity.Todo;
import se.lexicon.g58todoapp.repo.TodoRepository;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoDto create(TodoDto todoDto) {

        // Convert dto to entity
        Todo todo = new Todo(todoDto.title(), todoDto.description(), todoDto.completed(), todoDto.dueDate());

        //save entity
        Todo save = todoRepository.save(todo);


        // convert to dto.

        return new TodoDto(save.getId(),
                save.getTitle(),
                save.getDescription(),
                save.getDueDate(),
                save.getCompleted(),
                save.getCreatedAt(),
                save.getUpdatedAt(),
                save.getAssignedTo() != null ? save.getAssignedTo().getId() : null,
                save.getAttachments().size());
    }
}
