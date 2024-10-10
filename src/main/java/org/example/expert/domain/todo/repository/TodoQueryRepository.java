package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoProjectionDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoQueryRepository {

    Optional<Todo> findByIdWithUserByDsl(@Param("todoId") Long todoId);

    Page<TodoProjectionDto> findAllByTitleWithCreatedDateWithNickname(
            String title,
            String nickname,
            String startCreatedDate,
            String endCreatedDate,
            Pageable pageable);

}
