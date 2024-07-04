package com.sparta.everydrink.domain.post.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.everydrink.domain.liked.entity.ContentsTypeEnum;
import com.sparta.everydrink.domain.post.dto.PostResponseDto;
import com.sparta.everydrink.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sparta.everydrink.domain.liked.entity.QLiked.liked;
import static com.sparta.everydrink.domain.post.entity.QPost.post;
import static com.sparta.everydrink.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
@Slf4j(topic = "PostRepositoryImpl")
public class PostRepositoryImpl implements PostRepositoryQuery {

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
        log.info(pageable.getSort().toString());
        List<Long> userIds = followedUsers.stream().map(User::getId).collect(Collectors.toList());

        // Sort 객체 추출
        Sort sort = pageable.getSort();
        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);

        // Sort 객체에서 properties 추출
        for (Sort.Order order : sort) {
            System.out.println("Property: " + order.getProperty() + ", Direction: " + order.getDirection());
            if("username".equals(order.getProperty())){
                orderSpecifier = new OrderSpecifier<>(Order.DESC, post.user.username);
            }
        }

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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        Long total = Optional.ofNullable(queryFactory
                .select(post.count())
                .from(post)
                .where(post.user.id.in(userIds))
                .fetchOne()).orElse(0L);

        return PageableExecutionUtils.getPage(results, pageable, () -> total);
    }
}
