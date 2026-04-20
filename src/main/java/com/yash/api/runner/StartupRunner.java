package com.yash.api.runner;

import com.yash.api.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupRunner implements CommandLineRunner {

    private final WebhookService webhookService;

    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
        log.info("Application started. Running webhook workflow...");
        webhookService.executeWorkflow();
        log.info("Webhook workflow completed.");
    }
}
