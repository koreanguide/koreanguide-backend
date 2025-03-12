package kr.yuns.profile.data.entity;

import jakarta.persistence.*;
import kr.yuns.auth.data.entity.User;
import kr.yuns.profile.data.enums.Language;
import kr.yuns.profile.data.enums.SubwayLine;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String phoneNum;

    @Column
    @Enumerated(EnumType.STRING)
    private Language firstLang;

    @Column
    @Enumerated(EnumType.STRING)
    private Language secondLang;

    @Column
    @Enumerated(EnumType.STRING)
    private SubwayLine subwayLine;

    @Column
    private String subwayStation;

    @Column
    private String birth;

    @Column
    private String introduce;

    @Column
    @Builder.Default
    private boolean profileCompleteCouponUsed = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    @Builder.Default
    private boolean isPublic = true;
}