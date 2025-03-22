package kr.yuns.seoul.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import kr.yuns.auth.data.enums.SeoulCountry;
import kr.yuns.seoul.data.enums.RiverPark;

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