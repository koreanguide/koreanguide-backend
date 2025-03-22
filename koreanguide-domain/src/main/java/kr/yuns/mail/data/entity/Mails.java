package kr.yuns.mail.data.entity;

import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import kr.yuns.mail.data.enums.MailType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class Mails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailType type;

    @Column(nullable = false)
    private LocalDateTime sentAt;
}