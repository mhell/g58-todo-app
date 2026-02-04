package se.lexicon.g58todoapp.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(String message) {
        super(message);
    }
}
