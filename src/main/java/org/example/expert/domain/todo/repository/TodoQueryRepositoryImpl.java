package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoProjectionDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.todo.entity.QTodo.todo;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

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

    @Override
    public Page<TodoProjectionDto> findAllByTitleWithCreatedDateWithNickname(String title,
                                                                             String nickname,
                                                                             String startCreatedDate,
                                                                             String endCreatedDate,
                                                                             Pageable pageable) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (title != null && !title.isEmpty()) {
            booleanBuilder.and(todo.title.containsIgnoreCase(title));
        }

        QManager manager = QManager.manager;
        if (nickname != null && !nickname.isEmpty()) {
            booleanBuilder.and(
                    JPAExpressions
                            .select(manager.id)
                            .from(manager)
                            .where(manager.user.nickname.containsIgnoreCase(nickname))
                            .exists()
            );
        }

        if (startCreatedDate != null && endCreatedDate != null) {
            LocalDateTime startDate = LocalDate.parse(startCreatedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(endCreatedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23, 59, 59);
            booleanBuilder.and(todo.createdAt.between(startDate, endDate));
        }

        List<TodoProjectionDto> todos = jpaQueryFactory
                .select(
                        Projections.constructor(
                                TodoProjectionDto.class,
                                todo.title,
                                // 담당자 수
                                select(manager.user.count())
                                        .from(manager)
                                        .where(manager.todo.eq(todo)),
                                // 댓글 개수
                                select(comment.count())
                                        .from(comment)
                                        .where(comment.todo.eq(todo))
                        ))
                .from(todo)
                .join(todo.managers, manager)
                .where(booleanBuilder)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수 조회 (페이징 처리를 위해)
        long total = jpaQueryFactory
                .select(todo.count())
                .from(todo)
                .where(booleanBuilder)
                .fetchOne();

        return new PageImpl<>(todos, pageable, total);
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }

}
