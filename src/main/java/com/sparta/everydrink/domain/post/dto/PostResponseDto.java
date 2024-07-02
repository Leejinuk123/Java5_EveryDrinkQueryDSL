package com.sparta.everydrink.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.sparta.everydrink.domain.post.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
    private Long likeCount;

    @QueryProjection
    public PostResponseDto(Long id, String title, String content, String username, LocalDateTime createdAt, LocalDateTime modifiedAt, Long likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.likeCount = likeCount;
    }

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.username = post.getUser().getUsername();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.likeCount = post.getLikeCount();
    }
}