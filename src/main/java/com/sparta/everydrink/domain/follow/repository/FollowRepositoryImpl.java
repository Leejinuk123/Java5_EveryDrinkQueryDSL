package com.sparta.everydrink.domain.follow.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.everydrink.domain.follow.dto.TopFollowerResponseDto;
import com.sparta.everydrink.domain.follow.entity.Follow;
import com.sparta.everydrink.domain.follow.entity.QFollow;
import com.sparta.everydrink.domain.user.entity.QUser;
import com.sparta.everydrink.domain.user.entity.User;
import com.sparta.everydrink.domain.user.entity.UserStatusEnum;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Follow> checkDoubleFollow(User fromUser, User toUser) {
        QFollow follow = QFollow.follow;

        Follow result = queryFactory.select(follow)
                .where(follow.fromUser.eq(fromUser)
                        .and(follow.toUser.eq(toUser)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<TopFollowerResponseDto> getFollowerTop(int count) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        return queryFactory
                .select(Projections.bean(TopFollowerResponseDto.class,
                        follow.toUser.id.as("userId"),
                        user.username,
                        user.nickname,
                        follow.toUser.id.count().as("follower")))
                .from(follow)
                .join(user).on(user.id.eq(follow.toUser.id))
                .where(user.status.eq(UserStatusEnum.valueOf("ACTIVE")))
                .groupBy(user.id, user.username, user.nickname)
                .orderBy(follow.toUser.id.count().desc())
                .limit(count)
                .fetch();
    }
}
