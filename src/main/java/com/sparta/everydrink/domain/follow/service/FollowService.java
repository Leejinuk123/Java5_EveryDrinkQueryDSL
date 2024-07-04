package com.sparta.everydrink.domain.follow.service;

import com.sparta.everydrink.domain.common.CommonResponseDto;
import com.sparta.everydrink.domain.follow.dto.FollowRequestDto;
import com.sparta.everydrink.domain.follow.dto.FollowResponseDto;
import com.sparta.everydrink.domain.follow.dto.TopFollowerResponseDto;
import com.sparta.everydrink.domain.follow.entity.Follow;
import com.sparta.everydrink.domain.follow.repository.FollowRepository;
import com.sparta.everydrink.domain.post.dto.PostPageRequestDto;
import com.sparta.everydrink.domain.post.dto.PostResponseDto;
import com.sparta.everydrink.domain.post.entity.Post;
import com.sparta.everydrink.domain.post.repository.PostRepository;
import com.sparta.everydrink.domain.user.entity.User;
import com.sparta.everydrink.domain.user.repository.UserRepository;
import com.sparta.everydrink.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public FollowResponseDto followUser(FollowRequestDto followRequestDto, UserDetailsImpl user) {
        User currentUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User targetUser = userRepository.findByUsername(followRequestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("팔로우할 사용자를 찾을 수 없습니다."));

        if (currentUser.getUsername().equals(followRequestDto.getUsername())) {
            throw new IllegalArgumentException("본인은 팔로우할 수 없습니다.");
        }

//        if(followRepository.checkDoubleFollow(currentUser, targetUser).isPresent()) {
//            throw new IllegalArgumentException("이미 팔로우한 사용자입니다.");
//        }


        Follow follow = new Follow(currentUser, targetUser);
        followRepository.save(follow);

        return new FollowResponseDto(follow);

    }

    @Transactional
    public FollowResponseDto unfollowUser(FollowRequestDto followRequestDto, UserDetailsImpl user){
        User currentUser = userRepository.searchUser(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User targetUser = userRepository.searchUser(followRequestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("팔로우할 사용자를 찾을 수 없습니다."));

        Follow follow = followRepository.findByFromUserAndToUser(currentUser, targetUser)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 존재하지 않습니다."));

        followRepository.delete(follow);

        return new FollowResponseDto(follow);
    }

    @Transactional
    public Page<PostResponseDto> getFollowedUserPosts(UserDetailsImpl user, PostPageRequestDto requestDto) {
        User currentUser = userRepository.searchUser(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<User> followedUsers = followRepository.findByFromUser(currentUser)
                .stream()
                .map(Follow::getToUser)
                .collect(Collectors.toList());

        //작성자명 정렬 기능 추가

        Sort.Direction direction = Sort.Direction.DESC; //ASC 오름차순 , DESC 내림차순
        //- 생성일자 기준 최신 - 좋아요 많은 순

        // --- 정렬 방식 ---
        //CREATE  or  LIKED
        String sortBy = "created_at";
        if (requestDto.getSortBy().equals("CREATE")) {
            sortBy = "createdAt";
        } else if (requestDto.getSortBy().equals("USERNAME")) {
            sortBy = "username";
        } else
            throw new IllegalArgumentException("정렬은 CREATE 또는 USERNAME 만 입력 가능합니다.");

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(requestDto.getPage() - 1, requestDto.getSize(), sort);

        return postRepository.followedPostFindAll(followedUsers, pageable);

    }

    public List<TopFollowerResponseDto> getFollowerTopRank(int count) {
        if (count <= 0) throw new IllegalArgumentException("count 는 0보다 커야합니다.");
        return followRepository.getFollowerTop(count);
    }
}
