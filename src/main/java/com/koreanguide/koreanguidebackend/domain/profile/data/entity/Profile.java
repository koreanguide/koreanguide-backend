package com.koreanguide.koreanguidebackend.domain.profile.data.entity;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.Language;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.SubwayLine;
import lombok.*;

import javax.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private boolean isPublic = true;
}
