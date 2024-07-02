package com.sparta.everydrink.domain.comment.repository;

import com.sparta.everydrink.domain.comment.dto.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryQuery {
    Page<CommentResponseDto> likedCommentFindAll(Long user_id, Pageable pageable);
}
