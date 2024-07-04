package com.sparta.everydrink.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.everydrink.domain.comment.entity.QComment;
import com.sparta.everydrink.domain.liked.entity.QLiked;
import com.sparta.everydrink.domain.post.entity.QPost;
import com.sparta.everydrink.domain.user.dto.ProfileResponseDto;
import com.sparta.everydrink.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.sparta.everydrink.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryQuery{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ProfileResponseDto searchUserProfile(String username) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        QLiked liked = QLiked.liked;

        return jpaQueryFactory
                .select(Projections.bean(ProfileResponseDto.class,
                        user.username,
                        user.nickname,
                        liked.contentsType.as("ContentType"),
                        liked.contentsId.countDistinct().as("likedContentsCount")))
                .from(liked)
                .leftJoin(user).on(liked.user.username.eq(user.username))
                .where(user.username.eq(username))
                .groupBy(user.username, user.nickname, liked.contentsType)
                .fetchOne();
//                .select(Projections.bean(ProfileResponseDto.class,
//                        user.username,
//                        user.nickname,
//                        post.count().as("likedPosts"),
//                        comment.count().as("likedComments")))
//                .from(user)
//                .leftJoin(liked).on(liked.user.username.eq(user.username))
//                .leftJoin(post).on(liked.contentsId.eq(post.id)
//                        .and(liked.contentsType.eq(ContentsTypeEnum.POST)))
//                .leftJoin(comment).on(liked.contentsId.eq(comment.id)
//                        .and(liked.contentsType.eq(ContentsTypeEnum.COMMENT)))
//                .where(user.username.eq(username))
//                .fetchOne();
    }

    @Override
    public Optional<User> searchUser(String username) {
        return Optional.ofNullable(jpaQueryFactory
                .select(user)
                .from(user)
                .where(user.username.eq(username))
                .fetchOne());
    }
}
