package com.example.backend.repository;

import com.example.backend.entity.Geocache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeocacheRepository extends JpaRepository<Geocache, Integer> {
    List<Geocache> findByUserId(Integer userId);
}
