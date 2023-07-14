package com.board.post.service;

import com.board.post.domain.Post;
import com.board.post.dto.PostWriteRequest;
import com.board.post.repository.PostRepository;
import com.board.user.domain.User;
import com.board.user.exception.UserNotFoundException;
import com.board.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void write(PostWriteRequest dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Post post = Post.builder()
                .title(dto.getTitle())
                .writer(user.getNickname())
                .content(dto.getContent())
                .user(user)
                .build();
        postRepository.save(post);
    }

}
