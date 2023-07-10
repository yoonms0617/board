package com.board.user.controller;

import com.board.global.security.config.SecurityConfig;
import com.board.global.security.dto.AuthUser;
import com.board.global.security.util.JwtUtil;
import com.board.user.dto.UserSignupRequest;
import com.board.user.exception.DuplicateNicknameException;
import com.board.user.exception.DuplicateUsernameException;
import com.board.user.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ActiveProfiles(profiles = "test")
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token.expire-length}")
    private long accessTokenInMilliseconds;

    @Test
    @DisplayName("회원가입 요청을 정상적으로 처리한다")
    void signup() throws Exception {
        UserSignupRequest dto = new UserSignupRequest("yoonKun", "yoonms0617", "12345678");

        willDoNothing().given(userService).signup(any(UserSignupRequest.class));

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 입력값으로 회원가입 요청시 예외 메시지를 응답한다")
    void signup_invalidInputValue() throws Exception {
        UserSignupRequest dto = new UserSignupRequest("", "", "12345678");

        willDoNothing().given(userService).signup(any(UserSignupRequest.class));

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERR-COMMON001"))
                .andExpect(jsonPath("$.status").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청시 닉네임이 중복되면 예외 메시지를 응답한다")
    void signup_duplicateNickname() throws Exception {
        UserSignupRequest dto = new UserSignupRequest("yoonKun", "yoonms0617", "12345678");

        willThrow(new DuplicateNicknameException()).given(userService).signup(any(UserSignupRequest.class));

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ERR-USER001"))
                .andExpect(jsonPath("$.status").value("409"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 요청시 아이디가 중복되면 예외 메시지를 응답한다")
    void signup_duplicateUsername() throws Exception {
        UserSignupRequest dto = new UserSignupRequest("yoonKun", "yoonms0617", "12345678");

        willThrow(new DuplicateUsernameException()).given(userService).signup(any(UserSignupRequest.class));

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ERR-USER002"))
                .andExpect(jsonPath("$.status").value("409"))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 검사 요청을 정상적으로 처리한다")
    void checkNickname() throws Exception {
        willDoNothing().given(userService).checkNickname(any(String.class));

        mockMvc.perform(get("/api/user/check-nickname")
                        .param("nickname", "yoonKun")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Y"))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 검사 요청시 중복되면 예외 메시지를 응답한다")
    void checkNickname_duplicate() throws Exception {
        willThrow(new DuplicateNicknameException()).given(userService).checkNickname(any(String.class));

        mockMvc.perform(get("/api/user/check-nickname")
                        .param("nickname", "yoonKun")
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ERR-USER001"))
                .andExpect(jsonPath("$.status").value("409"))
                .andDo(print());
    }

    @Test
    @DisplayName("아이디 중복 검사 요청을 정상적으로 처리한다")
    void checkUsername() throws Exception {
        willDoNothing().given(userService).checkUsername(any(String.class));

        mockMvc.perform(get("/api/user/check-username")
                        .param("username", "yoonms0617")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Y"))
                .andDo(print());
    }

    @Test
    @DisplayName("아이디 중복 검사 요청시 중복되면 예외 메시지를 응답한다")
    void checkUsername_duplicate() throws Exception {
        willThrow(new DuplicateUsernameException()).given(userService).checkUsername(any(String.class));

        mockMvc.perform(get("/api/user/check-username")
                        .param("username", "yoonms0617")
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ERR-USER002"))
                .andExpect(jsonPath("$.status").value("409"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 요청을 정상적으로 처리하고 AccessToken을 응답한다")
    void login() throws Exception {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encoded = passwordEncoder.encode("12345678");
        AuthUser authUser = new AuthUser("yoonKun", "yoonms0617", encoded, "ROLE_MEMBER");

        Date iat = new Date();
        Date exp = new Date(iat.getTime() + accessTokenInMilliseconds);
        String accessToken = Jwts.builder()
                .claim("username", "yoonms0617")
                .claim("role", "ROLE_MEMBER")
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        given(userDetailsService.loadUserByUsername(any(String.class))).willReturn(authUser);
        given(jwtUtil.createAccessToken(any(String.class), any(String.class))).willReturn(accessToken);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "yoonms0617")
                        .param("password", "12345678")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("yoonKun"))
                .andExpect(jsonPath("$.token.accessToken").value(accessToken))
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 아이디 또는 비밀번호로 로그인을 요청하면 예외 메시지를 응답한다")
    void login_invalidUsernamePassword() throws Exception {
        willThrow(UsernameNotFoundException.class).given(userDetailsService).loadUserByUsername(any(String.class));

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "yoonms0617")
                        .param("password", "12345678")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERR-AUTH001"))
                .andExpect(jsonPath("$.status").value("400"))
                .andDo(print());
    }

}