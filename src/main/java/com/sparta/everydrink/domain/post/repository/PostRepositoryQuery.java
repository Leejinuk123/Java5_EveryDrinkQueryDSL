package com.sparta.everydrink.domain.post.repository;

import com.sparta.everydrink.domain.post.dto.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostRepositoryQuery {
    Page<PostResponseDto> likedPostFindAll(Long user_id, Pageable pageable);
}
