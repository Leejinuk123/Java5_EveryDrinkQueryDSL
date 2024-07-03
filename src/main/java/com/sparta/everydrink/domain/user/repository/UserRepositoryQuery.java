package com.sparta.everydrink.domain.user.repository;

import com.sparta.everydrink.domain.user.dto.ProfileResponseDto;

public interface UserRepositoryQuery {
    ProfileResponseDto searchUser(String username);
}
