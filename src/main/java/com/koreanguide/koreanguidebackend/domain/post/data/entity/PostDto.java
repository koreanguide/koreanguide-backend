package com.koreanguide.koreanguidebackend.domain.post.data.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PostDto {
    private String title;
    private String contents;
}
