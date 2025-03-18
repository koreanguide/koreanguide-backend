package kr.yuns.credit.data.entity;

import lombok.*;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import kr.yuns.auth.data.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private LocalDateTime recentUsed;
}