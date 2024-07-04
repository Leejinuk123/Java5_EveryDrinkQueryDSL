package com.sparta.everydrink.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopFollowerResponseDto {
    long userId;
    String username;
    String nickname;
    long follower;
//    int rank;
}
