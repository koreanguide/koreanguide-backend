package kr.yuns.credit.data.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import kr.yuns.auth.data.entity.User;
import kr.yuns.credit.data.enums.AccountProvider;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class BankAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountProvider accountProvider;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private LocalDateTime appliedAt;
}