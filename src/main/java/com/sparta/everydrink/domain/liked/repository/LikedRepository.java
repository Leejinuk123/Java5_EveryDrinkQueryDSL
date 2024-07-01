package com.sparta.everydrink.domain.liked.repository;

import com.sparta.everydrink.domain.liked.entity.Liked;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedRepository extends JpaRepository<Liked, Long>, LikedRepositoryQuery{
}
