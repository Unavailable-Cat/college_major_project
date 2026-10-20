package com.sanket.HealthMonitorProject.repository;

import com.sanket.HealthMonitorProject.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByHardwareIdOrderByRecordedAtDesc(String hardwareId);

    List<HealthRecord> findTop15ByHardwareIdOrderByRecordedAtDesc(String hardwareId);
}
