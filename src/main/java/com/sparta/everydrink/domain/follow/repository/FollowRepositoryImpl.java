package com.sparta.everydrink.domain.follow.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.everydrink.domain.follow.entity.Follow;
import com.sparta.everydrink.domain.follow.entity.QFollow;
import com.sparta.everydrink.domain.user.entity.QUser;
import com.sparta.everydrink.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryQuery{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Follow> checkDoubleFollow(User fromUser, User toUser) {
        QFollow follow = QFollow.follow;

        Follow result = queryFactory.selectFrom(follow)
                .where(follow.fromUser.eq(fromUser)
                        .and(follow.toUser.eq(toUser)))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
