package kr.yuns.file.data.entity;

import lombok.*;
import jakarta.persistence.*;
import kr.yuns.auth.data.entity.User;
import kr.yuns.file.data.enums.FileDivision;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User uploadedUser;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    @Builder.Default
    private FileDivision division = FileDivision.ETC_IMAGE;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}