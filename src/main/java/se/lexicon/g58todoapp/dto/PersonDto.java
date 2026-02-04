package se.lexicon.g58todoapp.dto;


import lombok.Builder;

@Builder
public record PersonDto(
        Long id, String name, String email
) {
}
