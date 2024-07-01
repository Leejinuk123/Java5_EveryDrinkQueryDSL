package com.sparta.everydrink.domain.liked.repository;

import com.sparta.everydrink.domain.liked.entity.ContentsTypeEnum;
import com.sparta.everydrink.domain.liked.entity.Liked;

import java.util.Optional;

public interface LikedRepositoryQuery {
    Optional<Liked> searchLiked(Long user_id, Long contentsId, ContentsTypeEnum contentsType);
}
