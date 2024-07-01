package com.sparta.everydrink.domain.liked.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.everydrink.domain.liked.entity.ContentsTypeEnum;
import com.sparta.everydrink.domain.liked.entity.Liked;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.sparta.everydrink.domain.liked.entity.QLiked.liked;

@Repository
@RequiredArgsConstructor
public class LikedRepositoryImpl implements LikedRepositoryQuery{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Liked> searchLiked(Long user_id, Long contentsId, ContentsTypeEnum contentsType) {
        return Optional.ofNullable(queryFactory.selectFrom(liked)
                .where(liked.user.id.eq(user_id)
                        .and(liked.contentsId.eq(contentsId))
                        .and(liked.contentsType.eq(contentsType)))
                .fetchOne());
    }
}
