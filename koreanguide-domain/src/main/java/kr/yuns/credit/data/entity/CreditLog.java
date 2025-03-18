package kr.yuns.credit.data.entity;

import lombok.*;
import jakarta.persistence.*;
import kr.yuns.credit.data.enums.TransactionContent;
import kr.yuns.credit.data.enums.TransactionType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class CreditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionContent transactionContent;

    @Column(nullable = false)
    private LocalDateTime date;
}