package com.sanket.HealthMonitorProject.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class HealthBatchRequest {
    private String hardwareId;
    private int totalSteps;
    private Instant sentAt;
    private List<HealthReadingDto> readings;
}
