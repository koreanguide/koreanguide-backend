package com.koreanguide.koreanguidebackend.domain.credit.data.entity;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.ReturningStatus;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionContent;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionType;
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
    private ReturningStatus returningStatus = ReturningStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column(nullable = true)
    private LocalDateTime updateDate;
}
