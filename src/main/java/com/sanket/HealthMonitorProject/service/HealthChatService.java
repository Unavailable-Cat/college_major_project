package com.sanket.HealthMonitorProject.service;

import com.sanket.HealthMonitorProject.dto.HealthChatRequest;
import com.sanket.HealthMonitorProject.model.HealthRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthChatService {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final HealthRecordService healthRecordService;
    private final String systemPrompt;

    public HealthChatService(
            ChatClient chatClient,
            ChatMemory chatMemory,
            HealthRecordService healthRecordService,
            @Value("${health.ai.system-prompt}") String systemPrompt) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.healthRecordService = healthRecordService;
        this.systemPrompt = systemPrompt;
    }

    public String chat(HealthChatRequest request) {
        if (request == null || request.getChatId() == null || request.getChatId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "chatId is required");
        }
//        if (request.getChatId().trim().length() > 36) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "chatId must not exceed 36 characters");
//        }
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question is required");
        }
        String hardwareId = request.getHardwareId();
        List<HealthRecord> records = hardwareId == null || hardwareId.isBlank()
                ? List.of()
                : healthRecordService.recentForHardware(hardwareId.trim());
        String context = records.isEmpty()
                ? "No health records were provided for this conversation."
                : records.stream()
                        .map(record -> String.format(
                                "time=%s, cadence=%.2f, SpO2=%.2f%%, instantHeartbeat=%.2f bpm, totalSteps=%d",
                                record.getRecordedAt(), record.getCadence(), record.getSpo2(),
                                record.getInstantHeartbeat(), record.getTotalSteps()))
                        .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .system(systemPrompt)
                //.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                //.advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, request.getChatId().trim()))
                .user(user -> user.text("""
                        Hardware ID: {hardwareId}
                        Health records (newest first):
                        {healthData}

                        User question: {question}
                        """)
                        .param("hardwareId", hardwareId == null ? "not specified" : hardwareId)
                        .param("healthData", context)
                        .param("question", request.getQuestion()))
                .call()
                .content();
    }
}
