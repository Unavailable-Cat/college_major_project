package com.sanket.HealthMonitorProject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthReadingDto {
    private float cadence;
    private float spo2;
    private float instantHeartbeat;
}
