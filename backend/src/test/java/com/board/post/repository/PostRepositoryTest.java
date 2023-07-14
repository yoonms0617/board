package com.board.post.repository;

import com.board.global.config.JpaAuditConfig;
import com.board.post.domain.Post;
import com.board.user.domain.User;
import com.board.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles(profiles = "test")
@Import(JpaAuditConfig.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .nickname("yoonKun")
                .username("yoonms0617")
                .password("12345678")
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("게시글을 저장한다")
    void post_save() {
        Post post = Post.builder()
                .title("안녕하세요 제목입니다.")
                .writer(user.getNickname())
                .content("안녕하세요 내용입니다.")
                .user(user)
                .build();

        Post actual = postRepository.save(post);

        assertThat(actual.getId()).isNotNull();
    }

}