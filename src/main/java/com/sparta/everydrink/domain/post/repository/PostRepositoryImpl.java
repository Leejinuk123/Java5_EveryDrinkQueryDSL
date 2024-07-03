package com.sparta.everydrink.domain.post.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.everydrink.domain.liked.entity.ContentsTypeEnum;
import com.sparta.everydrink.domain.post.dto.PostResponseDto;
import com.sparta.everydrink.domain.post.entity.QPost;
import com.sparta.everydrink.domain.user.entity.QUser;
import com.sparta.everydrink.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sparta.everydrink.domain.liked.entity.QLiked.liked;
import static com.sparta.everydrink.domain.post.entity.QPost.post;
import static com.sparta.everydrink.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryQuery{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostResponseDto> likedPostFindAll(Long user_id, Pageable pageable) {
        List<PostResponseDto> results = queryFactory
                .select(Projections.bean(PostResponseDto.class,
                        post.id,
                        post.title,
                        post.content,
                        user.username,
                        post.createdAt,
                        post.modifiedAt,
                        post.likeCount))
                .from(post)
                .join(post.user, user)
                .join(liked).on(post.id.eq(liked.contentsId)
                        .and(liked.contentsType.eq(ContentsTypeEnum.POST))
                        .and(liked.user.id.eq(user_id)))
                .orderBy(post.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Fetch the total count
        long total = Optional.ofNullable(queryFactory
                .select(post.count())
                .from(post)
                .join(post.user, user)
                .join(liked).on(post.id.eq(liked.contentsId)
                        .and(liked.contentsType.eq(ContentsTypeEnum.POST))
                        .and(liked.user.id.eq(user_id)))
                .fetchOne()).orElse(0L);

        return PageableExecutionUtils.getPage(results, pageable, () -> total);
    }

    @Override
    public Page<PostResponseDto> followedPostFindAll(List<User> followedUsers, Pageable pageable) {
        List<Long> userIds = followedUsers.stream().map(User::getId).collect(Collectors.toList());

        List<PostResponseDto> results = queryFactory
                .select(Projections.bean(PostResponseDto.class,
                        post.id,
                        post.title,
                        post.content,
                        user.username,
                        post.createdAt,
                        post.modifiedAt,
                        post.likeCount))
                .from(post)
                .leftJoin(post.user, user)
                .where(post.user.id.in(userIds))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(queryFactory
                .select(post.count())
                .from(post)
                .where(post.user.id.in(userIds))
                .fetchOne()).orElse(0L);

        return PageableExecutionUtils.getPage(results, pageable, () -> total);
    }

}
