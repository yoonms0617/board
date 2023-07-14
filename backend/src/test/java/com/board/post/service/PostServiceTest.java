package com.board.post.service;

import com.board.post.domain.Post;
import com.board.post.dto.PostWriteRequest;
import com.board.post.repository.PostRepository;
import com.board.user.domain.User;
import com.board.user.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글을 작성한다")
    void write() {
        User user = new User("yoonKun", "yoonms0617", "12345678");
        Post post = new Post("title", user.getNickname(), "content", user);
        PostWriteRequest dto = new PostWriteRequest("title", "content");

        given(userRepository.findByUsername(any(String.class))).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willReturn(post);

        postService.write(dto, "yoonms0617");

        then(userRepository).should(atLeastOnce()).findByUsername(any(String.class));
        then(postRepository).should(atLeastOnce()).save(any(Post.class));
    }

}