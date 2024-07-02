package com.sparta.everydrink.domain.user.repository;

import com.sparta.everydrink.domain.user.dto.ProfileResponseDto;
import com.sparta.everydrink.domain.user.entity.User;

public interface UserRepositoryQuery {
    ProfileResponseDto searchUser(String username);
}
