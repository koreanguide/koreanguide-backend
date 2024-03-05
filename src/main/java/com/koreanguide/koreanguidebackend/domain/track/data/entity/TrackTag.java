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
public class TrackTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tagName;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;
}
