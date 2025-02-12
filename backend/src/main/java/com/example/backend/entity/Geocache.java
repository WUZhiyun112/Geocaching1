package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "geocache")
public class Geocache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @NotNull
    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "type")
    private String type;

    @NotBlank
    @Column(name = "status")
    private String status;

    @Column(name = "found_at", updatable = false)
    private LocalDateTime foundAt = LocalDateTime.now();

    public Geocache(String code, String name, BigDecimal latitude, BigDecimal longitude, String status, String type, LocalDateTime foundAt) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.type = type;
        this.foundAt = foundAt;
        // 如果你有代码字段，设置它
        // this.code = code; // 如果需要的话可以添加
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getFoundAt() {
        return foundAt;
    }

    public void setFoundAt(LocalDateTime foundAt) {
        this.foundAt = foundAt;
    }

    // toString for easier debugging and logging
    @Override
    public String toString() {
        return "Geocache{" +
                "id=" + id +
                ", user=" + user.getUsername() +  // Assuming User class has a getUsername() method
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", foundAt=" + foundAt +
                '}';
    }
}
