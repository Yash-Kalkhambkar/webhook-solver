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
        String regNo = "REG12347";
        String email = "yashkalkhambkar@gmail.com";

        log.info("Starting webhook workflow...");

        GenerateResponse response = generateWebhook(name, regNo, email);
        if (response == null) {
            log.error("Failed to generate webhook. Aborting.");
            return;
        }

        int question = determineQuestion(regNo);
        log.info("Selected Question: {}", question);

        String finalQuery = "SELECT p.AMOUNT AS SALARY, " +
                "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                "d.DEPARTMENT_NAME " +
                "FROM PAYMENTS p " +
                "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                "ORDER BY p.AMOUNT DESC " +
                "LIMIT 1";

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
