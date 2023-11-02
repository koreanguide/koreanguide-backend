package com.koreanguide.koreanguidebackend.domain.post.data.repository;

import com.koreanguide.koreanguidebackend.domain.post.data.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
