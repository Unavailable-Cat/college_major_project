package com.sanket.HealthMonitorProject.service;

import com.sanket.HealthMonitorProject.dto.HealthBatchRequest;
import com.sanket.HealthMonitorProject.dto.HealthReadingDto;
import com.sanket.HealthMonitorProject.dto.HealthRecordResponse;
import com.sanket.HealthMonitorProject.dto.HealthRecordUpdateRequest;
import com.sanket.HealthMonitorProject.mapper.HealthRecordMapper;
import com.sanket.HealthMonitorProject.model.HealthRecord;
import com.sanket.HealthMonitorProject.repository.HealthRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.time.Instant;
import java.util.List;

@Service
public class HealthRecordService {
    private final HealthRecordRepository repository;
    private final HealthRecordMapper mapper;

    public HealthRecordService(HealthRecordRepository repository, HealthRecordMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public List<HealthRecordResponse> createBatch(HealthBatchRequest request) {
        if (request == null || request.getHardwareId() == null || request.getHardwareId().isBlank()) {
            //System.out.println();
            throw badRequest("hardwareId is required");
        }
        if (request.getTotalSteps() < 0) {
            throw badRequest("totalSteps must not be negative");
        }
        if (request.getReadings() == null || request.getReadings().isEmpty()) {
            throw badRequest("At least one reading is required");
        }

        if (request.getSentAt() == null) {
            throw badRequest("sentAt is required");
        }

        Instant sentAt = request.getSentAt();
        List<HealthRecord> records = new ArrayList<>(request.getReadings().size());
        for (int index = 0; index < request.getReadings().size(); index++) {
            Instant recordedAt = sentAt.minusSeconds(
                    (long) (request.getReadings().size() - index - 1) * 10);
            records.add(toEntity(request, request.getReadings().get(index), recordedAt));
        }
        return repository.saveAll(records).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<HealthRecordResponse> getAll(String hardwareId) {
        if(hardwareId == null || hardwareId.isBlank()) {
            throw badRequest("hardwareId is required");
        }
        List<HealthRecord> records = repository.findByHardwareIdOrderByRecordedAtDesc(hardwareId.trim());
        return records.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public HealthRecordResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Transactional
    public HealthRecordResponse update(Long id, HealthRecordUpdateRequest request) {
        if (request == null || request.getHardwareId() == null || request.getHardwareId().isBlank()) {
            throw badRequest("hardwareId is required");
        }
        if (request.getTotalSteps() < 0) {
            throw badRequest("totalSteps must not be negative");
        }
        validateReading(request.getCadence(), request.getSpo2(), request.getInstantHeartbeat());

        HealthRecord record = findById(id);
        record.setHardwareId(request.getHardwareId().trim());
        record.setCadence(request.getCadence());
        record.setSpo2(request.getSpo2());
        record.setInstantHeartbeat(request.getInstantHeartbeat());
        record.setTotalSteps(request.getTotalSteps());
        if (request.getRecordedAt() != null) {
            record.setRecordedAt(request.getRecordedAt());
        }
        return mapper.toResponse(repository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public List<HealthRecord> recentForHardware(String hardwareId) {
        return repository.findTop15ByHardwareIdOrderByRecordedAtDesc(hardwareId);
    }

    private HealthRecord toEntity(HealthBatchRequest request, HealthReadingDto reading, Instant recordedAt) {
        if (reading == null) {
            throw badRequest("Reading entries must not be null");
        }
        validateReading(reading.getCadence(), reading.getSpo2(), reading.getInstantHeartbeat());

        HealthRecord record = new HealthRecord();
        record.setHardwareId(request.getHardwareId().trim());
        record.setCadence(reading.getCadence());
        record.setSpo2(reading.getSpo2());
        record.setInstantHeartbeat(reading.getInstantHeartbeat());
        record.setTotalSteps(request.getTotalSteps());
        record.setRecordedAt(recordedAt);
        return record;
    }

    private void validateReading(float cadence, float spo2, float instantHeartbeat) {
        if (!Float.isFinite(cadence) || cadence < 0) {
            throw badRequest("cadence must be a finite non-negative number");
        }
        if (!Float.isFinite(spo2) || spo2 < 0 || spo2 > 100) {
            throw badRequest("spo2 must be between 0 and 100");
        }
        if (!Float.isFinite(instantHeartbeat) || instantHeartbeat < 0) {
            throw badRequest("instantHeartbeat must be a finite positive number");
        }
    }

    private HealthRecord findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health record not found"));
    }

    private ResponseStatusException badRequest(String message) {
        System.out.println(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
