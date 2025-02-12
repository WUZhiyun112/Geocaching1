package com.example.geocaching1;



import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Geocache {
    private String code;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String status;
    private String type;
    private LocalDateTime foundAt;

    public Geocache(String code, String name, BigDecimal latitude, BigDecimal longitude, String status, String type, LocalDateTime foundAt) {
        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.type = type;
        this.foundAt = foundAt;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getFoundAt() {
        return foundAt;
    }
}
