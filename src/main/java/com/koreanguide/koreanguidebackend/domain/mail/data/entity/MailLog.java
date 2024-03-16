package com.koreanguide.koreanguidebackend.domain.mail.data.entity;

import com.koreanguide.koreanguidebackend.domain.mail.data.enums.MailType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class MailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailType mailType;

    @Column(nullable = false)
    private LocalDateTime sentAt;
}
