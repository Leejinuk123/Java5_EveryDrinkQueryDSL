package com.sparta.everydrink.domain.follow.service;

import com.sparta.everydrink.domain.follow.dto.FollowRequestDto;
import com.sparta.everydrink.domain.follow.dto.FollowResponseDto;
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
        User currentUser = userRepository.searchUser(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User targetUser = userRepository.searchUser(followRequestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("팔로우할 사용자를 찾을 수 없습니다."));

        if (currentUser.getUsername().equals(followRequestDto.getUsername())) {
            throw new IllegalArgumentException("본인은 팔로우할 수 없습니다.");
        }

        if(followRepository.checkDoubleFollow(currentUser, targetUser).isPresent()) {
            throw new IllegalArgumentException("이미 팔로우한 사용자입니다.");
        }


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

        Pageable pageable = PageRequest.of(requestDto.getPage() - 1, requestDto.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PostResponseDto> posts = postRepository.followedPostFindAll(followedUsers, pageable);

        return posts;

//        return posts.stream()
//                .map(PostResponseDto::new)
//                .collect(Collectors.toList());

    }
}
