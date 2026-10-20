package com.sanket.HealthMonitorProject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "health_records", indexes = {
        @Index(name = "idx_health_records_hardware_time", columnList = "hardware_id, recorded_at")
})
@Getter
@Setter
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hardwareId;
    private float cadence;
    private float spo2;
    private float instantHeartbeat;
    private int totalSteps;
    private Instant recordedAt;
}
