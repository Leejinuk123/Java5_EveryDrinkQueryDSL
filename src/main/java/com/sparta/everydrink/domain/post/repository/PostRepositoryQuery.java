package com.sparta.everydrink.domain.post.repository;

import com.sparta.everydrink.domain.post.dto.PostResponseDto;
import com.sparta.everydrink.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostRepositoryQuery {
    Page<PostResponseDto> likedPostFindAll(Long user_id, Pageable pageable);

    Page<PostResponseDto> followedPostFindAll(List<User> followedUsers, Pageable pageable, String sortBy);
}
