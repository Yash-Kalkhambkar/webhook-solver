package com.yash.api.runner;

import com.yash.api.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupRunner implements CommandLineRunner {

    private final WebhookService webhookService;

    @Value("${app.workflow.enabled:true}")
    private boolean workflowEnabled;

    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
        if (!workflowEnabled) {
            log.info("Webhook workflow disabled via configuration. Skipping execution.");
            return;
        }

        log.info("Application started. Running webhook workflow...");
        webhookService.executeWorkflow();
        log.info("Webhook workflow completed.");
    }
}
