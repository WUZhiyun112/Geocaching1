package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_statistics")
public class UserStatistics {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_found", nullable = false)
    private Integer totalFound = 0;

    // Getters and Setters
}
