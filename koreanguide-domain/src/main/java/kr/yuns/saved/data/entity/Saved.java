package kr.yuns.saved.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import kr.yuns.auth.data.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Saved {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private boolean useAble;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}