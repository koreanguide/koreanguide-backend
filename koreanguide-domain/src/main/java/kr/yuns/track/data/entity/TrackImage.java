package kr.yuns.track.data.entity;

import lombok.*;
import java.time.LocalDateTime;

import jakarta.persistence.*;

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
    @Builder.Default
    private boolean useAble = true;

    @Column
    private LocalDateTime disableDt;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;
}