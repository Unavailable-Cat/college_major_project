package com.sanket.HealthMonitorProject;

import com.sanket.HealthMonitorProject.dto.HealthBatchRequest;
import com.sanket.HealthMonitorProject.dto.HealthReadingDto;
import com.sanket.HealthMonitorProject.dto.HealthRecordResponse;
import com.sanket.HealthMonitorProject.service.HealthRecordService;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:healthmonitor;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.ai.openai.api-key=test-key"
})
class HealthMonitorProjectApplicationTests {
	@Autowired
	private HealthRecordService healthRecordService;
	@Autowired
	private ChatMemory chatMemory;

	@Test
	void contextLoads() {
	}

	@Test
	void batchReadingsAreStoredAtTenSecondIntervalsBeforeSentAt() {
		Instant sentAt = Instant.parse("2026-10-19T17:00:00Z");
		HealthBatchRequest request = new HealthBatchRequest();
		request.setHardwareId("test-device");
		request.setTotalSteps(1200);
		request.setSentAt(sentAt);
		request.setReadings(List.of(reading(72, 98, 80), reading(75, 97, 82), reading(77, 97, 84)));

		List<HealthRecordResponse> saved = healthRecordService.createBatch(request);

		assertEquals(3, saved.size());
		assertEquals(sentAt.minusSeconds(20), saved.get(0).getRecordedAt());
		assertEquals(sentAt.minusSeconds(10), saved.get(1).getRecordedAt());
		assertEquals(sentAt, saved.get(2).getRecordedAt());
	}

	@Test
	void chatMemoryPersistsMessagesByChatId() {
		String chatId = java.util.UUID.randomUUID().toString();
		chatMemory.add(chatId, new UserMessage("Show my recent readings"));

		assertEquals(1, chatMemory.get(chatId).size());

		chatMemory.clear(chatId);
	}

	private HealthReadingDto reading(float cadence, float spo2, float heartbeat) {
		HealthReadingDto reading = new HealthReadingDto();
		reading.setCadence(cadence);
		reading.setSpo2(spo2);
		reading.setInstantHeartbeat(heartbeat);
		return reading;
	}
}
