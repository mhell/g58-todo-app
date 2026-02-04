package se.lexicon.g58todoapp.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TodoDto(
        Long id,
        String title,
        String description,
        LocalDateTime dueDate,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long assignedToId,
        int numberOfAttachments
) {
}
