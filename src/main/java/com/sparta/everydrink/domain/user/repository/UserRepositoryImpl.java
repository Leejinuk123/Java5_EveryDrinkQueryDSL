package com.sparta.everydrink.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.everydrink.domain.comment.dto.CommentResponseDto;
import com.sparta.everydrink.domain.comment.entity.QComment;
import com.sparta.everydrink.domain.liked.entity.ContentsTypeEnum;
import com.sparta.everydrink.domain.liked.entity.QLiked;
import com.sparta.everydrink.domain.post.entity.QPost;
import com.sparta.everydrink.domain.user.dto.ProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sparta.everydrink.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryQuery{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ProfileResponseDto searchUser(String username) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        QLiked liked = QLiked.liked;

        // 사용자가 좋아요한 게시글 수 조회
        Long likedPosts = jpaQueryFactory
                .select(post.count())
                .from(post)
                .join(liked).on(post.id.eq(liked.contentsId)
                        .and(liked.contentsType.eq(ContentsTypeEnum.POST))
                        .and(liked.user.username.eq(username)))
                .fetchOne();

        // 사용자가 좋아요한 댓글 수 조회
        Long likedComments = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .join(liked).on(comment.id.eq(liked.contentsId)
                        .and(liked.contentsType.eq(ContentsTypeEnum.COMMENT))
                        .and(liked.user.username.eq(username)))
                .fetchOne();

        // 사용자 정보 및 좋아요한 게시글 및 댓글 수 조회
        ProfileResponseDto dto = jpaQueryFactory
                .select(Projections.bean(ProfileResponseDto.class,
                        user.username,
                        user.nickname))
                .from(user)
                .where(user.username.eq(username))
                .fetchOne();

        dto.setLikedPosts(likedPosts);
        dto.setLikedComments(likedComments);

        return dto;
    }
}
