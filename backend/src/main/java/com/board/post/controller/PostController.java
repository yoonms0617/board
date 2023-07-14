package com.board.post.controller;

import com.board.post.dto.PostWriteRequest;
import com.board.post.service.PostService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/write")
    @Secured("ROLE_MEMBER")
    public ResponseEntity<Void> write(@Valid @RequestBody PostWriteRequest dto, @AuthenticationPrincipal String username) {
        postService.write(dto, username);
        return ResponseEntity.ok().build();
    }

}
