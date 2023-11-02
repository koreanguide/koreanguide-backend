package com.koreanguide.koreanguidebackend.domain.post.service;

import com.koreanguide.koreanguidebackend.domain.post.data.dto.PostUpdateDto;
import com.koreanguide.koreanguidebackend.domain.post.data.entity.PostDto;
import com.koreanguide.koreanguidebackend.domain.post.data.dto.PostResponseDto;

public interface PostService {
    PostResponseDto getPost(Long id);
    PostResponseDto newPost(PostDto postDto);
    PostResponseDto modifyPost(PostUpdateDto postUpdateDto);
    void deletePost(Long id) throws Exception;
}
