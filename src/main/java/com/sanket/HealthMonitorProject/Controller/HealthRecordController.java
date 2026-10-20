package com.sanket.HealthMonitorProject.Controller;

import com.sanket.HealthMonitorProject.dto.HealthBatchRequest;
import com.sanket.HealthMonitorProject.dto.HealthRecordResponse;
import com.sanket.HealthMonitorProject.dto.HealthRecordUpdateRequest;
import com.sanket.HealthMonitorProject.service.HealthRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/health-records")
public class HealthRecordController {
    private final HealthRecordService service;

    public HealthRecordController(HealthRecordService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<HealthRecordResponse> createBatch(@RequestBody HealthBatchRequest request) {
        return service.createBatch(request);
    }

    @GetMapping
    public List<HealthRecordResponse> getAll(
            @RequestParam(required = false) String hardwareId) {
        return service.getAll(hardwareId);
    }

    @GetMapping("/{id}")
    public HealthRecordResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public HealthRecordResponse update(
            @PathVariable Long id,
            @RequestBody HealthRecordUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
