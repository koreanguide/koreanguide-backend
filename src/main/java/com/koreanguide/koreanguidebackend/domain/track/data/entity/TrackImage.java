package com.koreanguide.koreanguidebackend.domain.track.data.entity;

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
public class TrackImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean useAble;

    @Column(nullable = false)
    private LocalDateTime uploadedDt;

    @Column
    private LocalDateTime disableDt;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;
}
