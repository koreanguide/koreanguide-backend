package com.koreanguide.koreanguidebackend.domain.credit.data.entity;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
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
public class BankAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
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
