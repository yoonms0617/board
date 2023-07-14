package com.board.global.security.filter;

import com.board.global.security.util.JwtUtil;

import io.jsonwebtoken.Claims;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String[] NO_TOKEN_REQUEST_LIST = {
            "/api/user/login", "/api/user/signup", "/api/user/check-nickname", "/api/user/check-username"
    };

    private final JwtUtil jwtUtil;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            String accessToken = extractAccessToken(request);
            jwtUtil.validateToken(accessToken);
            Claims payload = jwtUtil.getPayload(accessToken);
            Authentication authentication = createAuthentication(payload);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
        }
    }

    private Authentication createAuthentication(Claims payload) {
        String username = (String) payload.get("username");
        String role = (String) payload.get("role");
        return UsernamePasswordAuthenticationToken.authenticated(username, null, AuthorityUtils.createAuthorityList(role));
    }

    /**
     * 현재 요청의 Authorization헤더에 토큰 타입이 Bearer인 Access Token을 추출한다.
     *
     * @param request 현재 HTTP 요청
     * @return 현재 요청의 Authoriztion 헤더에 Bearer 타입인 Access Token을 반환, 없다면 null을 반환
     */
    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer")) {
            return header.substring("Bearer".length()).trim();
        }
        return null;
    }

    /**
     * NO_TOKEN_REQUEST_URI에 있는 요청 URI는 Access Token이 필요없는 요청 목록으로 JwtAuthorizationFilter가 동작할 필요가 없다.
     * true를 반환하면 JwtAuthorizationFilter의 doFilterInternal 메소드가 동작하지 않고 false를 반환하면 동작한다.
     *
     * @param request 현재 HTTP 요청
     * @return 현재 요청 URI가 NO_TOKEN_REQUEST_URI에 포함하면 true, 포함하지 않으면 false를 반환
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (String uri : NO_TOKEN_REQUEST_LIST) {
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith(uri)) {
                return true;
            }
        }
        return false;
    }

}
