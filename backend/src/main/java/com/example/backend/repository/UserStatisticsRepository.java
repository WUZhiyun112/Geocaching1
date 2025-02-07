package com.example.backend.repository;

import com.example.backend.entity.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Integer> {
}
