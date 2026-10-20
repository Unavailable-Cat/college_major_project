package com.sanket.HealthMonitorProject.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class HealthRecordResponse {
    private Long id;
    private String hardwareId;
    private float cadence;
    private float spo2;
    private float instantHeartbeat;
    private int totalSteps;
    private Instant recordedAt;
}
