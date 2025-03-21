package kr.yuns.track.data.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.*;
import kr.yuns.auth.data.entity.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean agreePublicTerms;

    @Column(nullable = false)
    private LocalDateTime agreePublicTermsDt;

    @Column(nullable = false)
    private boolean agreeTerms;

    @Column(nullable = false)
    private LocalDateTime agreeTermsDt;

    @Column(nullable = false)
    private boolean agreePrivacyPolicy;

    @Column(nullable = false)
    private LocalDateTime agreePrivacyPolicyDt;

    @Column(nullable = false)
    private String trackTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String trackContent;

    @Column(nullable = false)
    private String trackPreview;

    @Column(nullable = false)
    private String primaryImageUrl;

    @Column(nullable = false)
    private boolean star;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean visible;

    @Column(nullable = false)
    private boolean useAble;

    @Column(nullable = false)
    private boolean blocked;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private boolean autoTranslate = true;

    @Column
    private String blockedReason;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}