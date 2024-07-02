package com.sparta.everydrink.domain.comment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.everydrink.domain.comment.dto.CommentResponseDto;
import com.sparta.everydrink.domain.liked.entity.ContentsTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.sparta.everydrink.domain.comment.entity.QComment.comment;
import static com.sparta.everydrink.domain.liked.entity.QLiked.liked;
import static com.sparta.everydrink.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryQuery{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentResponseDto> likedCommentFindAll(Long user_id, Pageable pageable) {

//        private Long id;
//        private String content;
//        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//        private LocalDateTime createdAt;
//        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//        private LocalDateTime updatedAt;
//
//
//        private Long likeCount;
        List<CommentResponseDto> results = queryFactory
                .select(Projections.bean(CommentResponseDto.class,
                        comment.id,
                        comment.content,
                        comment.createdAt,
                        comment.modifiedAt,
                        comment.likeCount))
                .from(comment)
                .join(comment.user, user)
                .join(liked).on(comment.id.eq(liked.contentsId)
                        .and(liked.contentsType.eq(ContentsTypeEnum.COMMENT))
                        .and(liked.user.id.eq(user_id)))
                .orderBy(comment.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Fetch the total count
        long total = Optional.ofNullable(queryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.user, user)
                .join(liked).on(comment.id.eq(liked.contentsId)
                        .and(liked.contentsType.eq(ContentsTypeEnum.COMMENT))
                        .and(liked.user.id.eq(user_id)))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(results, pageable, total);
    }
}
