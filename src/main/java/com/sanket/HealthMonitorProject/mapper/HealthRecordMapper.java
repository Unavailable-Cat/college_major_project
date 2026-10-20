package com.sanket.HealthMonitorProject.mapper;

import com.sanket.HealthMonitorProject.dto.HealthRecordResponse;
import com.sanket.HealthMonitorProject.model.HealthRecord;
import org.springframework.stereotype.Component;

@Component
public class HealthRecordMapper {
    public HealthRecordResponse toResponse(HealthRecord record) {
        HealthRecordResponse response = new HealthRecordResponse();
        response.setId(record.getId());
        response.setHardwareId(record.getHardwareId());
        response.setCadence(record.getCadence());
        response.setSpo2(record.getSpo2());
        response.setInstantHeartbeat(record.getInstantHeartbeat());
        response.setTotalSteps(record.getTotalSteps());
        response.setRecordedAt(record.getRecordedAt());
        return response;
    }
}
