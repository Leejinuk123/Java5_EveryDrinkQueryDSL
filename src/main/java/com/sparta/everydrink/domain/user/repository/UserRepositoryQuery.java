package com.sparta.everydrink.domain.user.repository;

import com.sparta.everydrink.domain.user.dto.ProfileResponseDto;
import com.sparta.everydrink.domain.user.entity.User;

import java.util.Optional;

public interface UserRepositoryQuery {
    ProfileResponseDto searchUserProfile(String username);

    Optional<User> searchUser(String username);
}
