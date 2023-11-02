package com.koreanguide.koreanguidebackend.domain.post.service.impl;

import com.koreanguide.koreanguidebackend.domain.post.data.entity.PostDto;
import com.koreanguide.koreanguidebackend.domain.post.data.dto.PostResponseDto;
import com.koreanguide.koreanguidebackend.domain.post.data.dto.PostUpdateDto;
import com.koreanguide.koreanguidebackend.domain.post.data.entity.Post;
import com.koreanguide.koreanguidebackend.domain.post.data.repository.PostRepository;
import com.koreanguide.koreanguidebackend.domain.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).get();

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(post.getId());
        postResponseDto.setTitle(post.getTitle());
        postResponseDto.setContents(post.getContents());

        return postResponseDto;
    }

    @Override
    public PostResponseDto newPost(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContents(postDto.getContents());

        Post savedPost = postRepository.save(post);

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(savedPost.getId());
        postResponseDto.setTitle(savedPost.getTitle());
        postResponseDto.setContents(savedPost.getContents());

        return postResponseDto;
    }

    @Override
    public PostResponseDto modifyPost(PostUpdateDto postUpdateDto) {
        Post post = postRepository.findById(postUpdateDto.getId()).get();
        post.setTitle(postUpdateDto.getTitle());
        post.setContents(postUpdateDto.getContents());
        Post modifiedPost = postRepository.save(post);

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(modifiedPost.getId());
        postResponseDto.setTitle(modifiedPost.getTitle());
        postResponseDto.setContents(modifiedPost.getContents());

        return postResponseDto;
    }

    @Override
    public void deletePost(Long id) throws Exception {
        postRepository.deleteById(id);
    }
}
