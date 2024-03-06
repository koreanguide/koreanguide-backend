package com.koreanguide.koreanguidebackend.domain.assistant.data.entity;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class AssistantLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private Long promptTokens;

    @Column(nullable = false)
    private Long completionTokens;

    @Column(nullable = false)
    private Long totalTokens;

    @Column(nullable = false)
    private LocalDateTime usedAt;
}
