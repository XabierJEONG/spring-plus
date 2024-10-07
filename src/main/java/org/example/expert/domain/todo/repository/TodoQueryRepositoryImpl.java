package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUserByDsl(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory.
                select(todo)
                .from(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(
                        todoIdEq(todoId)
                )
                .fetchOne());

    }

    private BooleanExpression todoIdEq (Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }
}
