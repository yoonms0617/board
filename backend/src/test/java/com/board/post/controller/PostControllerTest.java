package com.board.post.controller;

import com.board.global.security.config.SecurityConfig;
import com.board.global.security.exception.ExpiredTokenException;
import com.board.global.security.exception.InvalidTokenException;
import com.board.global.security.util.JwtUtil;
import com.board.post.dto.PostWriteRequest;
import com.board.post.service.PostService;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import java.util.Date;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
@ActiveProfiles(profiles = "test")
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PostService postService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token.expire-length}")
    private long accessTokenInMilliseconds;

    @Test
    @DisplayName("글 작성 요청을 정상적으로 처리한다")
    void write() throws Exception {
        PostWriteRequest request = new PostWriteRequest("title", "content");

        String accessToken = createAccessToken();
        Claims payload = createPayload(accessToken);

        willDoNothing().given(jwtUtil).validateToken(any(String.class));
        given(jwtUtil.getPayload(any(String.class))).willReturn(payload);

        mockMvc.perform(post("/api/post/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 Access Token을 포함한 글작성 요청시 예외 메시지를 응답한다")
    void write_invalidAccessToken() throws Exception {
        PostWriteRequest request = new PostWriteRequest("title", "content");

        String invalidAccessToken = "";

        willThrow(new InvalidTokenException()).given(jwtUtil).validateToken(any(String.class));

        mockMvc.perform(post("/api/post/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header("Authorization", "Bearer " + invalidAccessToken)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("ERR-AUTH002"))
                .andExpect(jsonPath("$.status").value("401"))
                .andDo(print());
    }

    @Test
    @DisplayName("만료된 Access Token을 포함한 글작성 요청시 예외 메세지를 응답한다")
    void write_expiredAccessToken() throws Exception {
        PostWriteRequest request = new PostWriteRequest("title", "content");

        Date iat = new Date();
        Date exp = new Date(iat.getTime() - 1);
        String expiredAccessToken = Jwts.builder()
                .claim("username", "yoonms0617")
                .claim("role", "ROLE_MEMBER")
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        willThrow(new ExpiredTokenException()).given(jwtUtil).validateToken(any(String.class));

        mockMvc.perform(post("/api/post/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header("Authorization", "Bearer " + expiredAccessToken)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("ERR-AUTH003"))
                .andExpect(jsonPath("$.status").value("401"))
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 입력값으로 글작성 요청시 예외 메시지를 응답한다")
    void write_invalidInputValue() throws Exception {
        PostWriteRequest request = new PostWriteRequest("", "");

        String accessToken = createAccessToken();
        Claims payload = createPayload(accessToken);

        willDoNothing().given(jwtUtil).validateToken(any(String.class));
        given(jwtUtil.getPayload(any(String.class))).willReturn(payload);

        mockMvc.perform(post("/api/post/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERR-COMMON001"))
                .andExpect(jsonPath("$.status").value("400"))
                .andDo(print());
    }

    private String createAccessToken() {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + accessTokenInMilliseconds);
        return Jwts.builder()
                .claim("username", "yoonms0617")
                .claim("role", "ROLE_MEMBER")
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims createPayload(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(accessToken).getBody();
    }

}