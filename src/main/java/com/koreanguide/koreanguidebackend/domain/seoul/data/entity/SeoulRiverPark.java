package com.koreanguide.koreanguidebackend.domain.seoul.data.entity;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.RiverPark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class SeoulRiverPark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String parkKo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RiverPark parkEn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeoulCountry parkCountry;

    @Column(nullable = false)
    private String parkAddress;

    @Column(nullable = false)
    private String parkX;

    @Column(nullable = false)
    private String parkY;

    @Column(nullable = false)
    private String parkService;

    @Column(nullable = false)
    private String parklength;

    @Column(nullable = false)
    private String parkArea;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
