package com.koreanguide.koreanguidebackend.domain.post.controller;

import com.koreanguide.koreanguidebackend.domain.post.data.dto.PostUpdateDto;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import com.koreanguide.koreanguidebackend.domain.post.data.entity.PostDto;
import com.koreanguide.koreanguidebackend.domain.post.data.dto.PostResponseDto;
import com.koreanguide.koreanguidebackend.domain.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<PostResponseDto> getPost(Long id) {
        PostResponseDto postResponseDto = postService.getPost(id);

        return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);
    }

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-AUTH-TOKEN",
                    value = "사용자 접근 Token",
                    required = true,
                    dataType = "String",
                    paramType = "header")
    })
    public ResponseEntity<PostResponseDto> newPost(@RequestBody PostDto postDto) {
        PostResponseDto postResponseDto = postService.newPost(postDto);

        return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);
    }

    @PutMapping
    public ResponseEntity<PostResponseDto> modifyPost(@RequestBody PostUpdateDto postUpdateDto) throws Exception {
        PostResponseDto postResponseDto = postService.modifyPost(postUpdateDto);

        return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);
    }

    @DeleteMapping
    public ResponseEntity<String> deletePost(Long id) throws Exception {
        postService.deletePost(id);

        return ResponseEntity.status(HttpStatus.OK).body("삭제가 정상적으로 완료되었습니다.");
    }
}
