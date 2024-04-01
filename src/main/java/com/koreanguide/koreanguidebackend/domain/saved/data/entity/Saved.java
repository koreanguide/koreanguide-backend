package com.koreanguide.koreanguidebackend.domain.saved.data.entity;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
