package com.sparta.everydrink.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileResponseDto {
    private String username;
    private String nickname;
    private Long likedPosts;
    private Long likedComments;

    public ProfileResponseDto(String username, String nickname, Long likedPosts, Long likedComments) {
        this.username = username;
        this.nickname = nickname;
        this.likedPosts = likedPosts;
        this.likedComments = likedComments;
    }
}
