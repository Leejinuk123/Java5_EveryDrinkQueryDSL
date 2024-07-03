package com.sparta.everydrink.domain.follow.repository;

import com.sparta.everydrink.domain.follow.entity.Follow;
import com.sparta.everydrink.domain.user.entity.User;

import java.util.Optional;

public interface FollowRepositoryQuery {
    Optional<Follow> checkDoubleFollow(User fromUser, User toUser);
}
