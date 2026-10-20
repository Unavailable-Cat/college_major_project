package com.sanket.HealthMonitorProject.Controller;

import com.sanket.HealthMonitorProject.dto.HealthChatRequest;
import com.sanket.HealthMonitorProject.service.HealthChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/health-chat")
public class HealthChatController {
    private final HealthChatService service;

    public HealthChatController(HealthChatService service) {
        this.service = service;
    }

    @PostMapping//(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String chat(@RequestBody HealthChatRequest request) {
        return service.chat(request);
    }
}
