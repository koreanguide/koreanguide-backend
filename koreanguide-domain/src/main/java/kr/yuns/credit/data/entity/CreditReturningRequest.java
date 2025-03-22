package kr.yuns.credit.data.entity;

import lombok.*;

import jakarta.persistence.*;
import kr.yuns.auth.data.entity.User;
import kr.yuns.credit.data.enums.ReturningStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class CreditReturningRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReturningStatus returningStatus = ReturningStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column(nullable = true)
    private LocalDateTime updateDate;
}