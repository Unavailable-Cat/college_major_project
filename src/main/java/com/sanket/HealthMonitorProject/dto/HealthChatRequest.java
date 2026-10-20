package com.sanket.HealthMonitorProject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthChatRequest {
    private String chatId;
    private String hardwareId;
    private String question;
}
