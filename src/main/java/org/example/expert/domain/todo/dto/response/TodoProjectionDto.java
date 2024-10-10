package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoProjectionDto {

    private String title;
    private Long user;
    private Long comment;

    public TodoProjectionDto(String title, Long user, Long comment) {
        this.title = title;
        this.user = user;
        this.comment = comment;
    }
}
