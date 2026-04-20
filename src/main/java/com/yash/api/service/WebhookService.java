package com.yash.api.service;

import com.yash.api.dto.GenerateRequest;
import com.yash.api.dto.GenerateResponse;
import com.yash.api.dto.SubmitRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class WebhookService {

    private final WebClient webClient;

    public WebhookService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void executeWorkflow() {
        String name = "Yash Kalkhambkar";
        String regNo = "REG12348";
        String email = "yashkalkhambkar@gmail.com";

        log.info("Starting webhook workflow...");

        GenerateResponse response = generateWebhook(name, regNo, email);
        if (response == null) {
            log.error("Failed to generate webhook. Aborting.");
            return;
        }

        int question = determineQuestion(regNo);
        log.info("Selected Question: {}", question);

        String finalQuery = "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, " +
                "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                "FROM EMPLOYEE e " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "LEFT JOIN EMPLOYEE e2 ON e.DEPARTMENT = e2.DEPARTMENT " +
                "AND e2.DOB > e.DOB " +
                "GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME " +
                "ORDER BY e.EMP_ID DESC";

        submitSolution(response.getAccessToken(), response.getWebhook(), finalQuery);
    }

    public GenerateResponse generateWebhook(String name, String regNo, String email) {
        log.info("Calling generateWebhook API...");
        GenerateResponse response = webClient.post()
                .uri("/hiring/generateWebhook/JAVA")
                .bodyValue(new GenerateRequest(name, regNo, email))
                .retrieve()
                .bodyToMono(GenerateResponse.class)
                .onErrorResume(e -> {
                    log.error("Error generating webhook: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();

        if (response != null) {
            log.info("Webhook URL: {}", response.getWebhook());
            log.info("Access Token received: {}", response.getAccessToken() != null ? "yes" : "null");
        }
        return response;
    }

    public int determineQuestion(String regNo) {
        try {
            String lastTwo = regNo.substring(regNo.length() - 2);
            int num = Integer.parseInt(lastTwo);
            int question = (num % 2 == 0) ? 2 : 1;
            log.info("RegNo last two digits: {} → Question {}", num, question);
            return question;
        } catch (Exception e) {
            log.warn("Could not parse regNo '{}', defaulting to Question 1", regNo);
            return 1;
        }
    }

    public void submitSolution(String accessToken, String webhookUrl, String finalQuery) {
        log.info("Submitting solution to: {}", webhookUrl);

        String result = WebClient.create()
                .post()
                .uri(webhookUrl)
                .header("Authorization", accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(new SubmitRequest(finalQuery))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("Error submitting solution: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();

        log.info("Submission response: {}", result);
    }
}
