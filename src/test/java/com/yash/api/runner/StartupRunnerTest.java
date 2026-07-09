package com.yash.api.runner;

import com.yash.api.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartupRunnerTest {

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private StartupRunner startupRunner;

    @Test
    void shouldNotRunWorkflowWhenDisabled() {
        ReflectionTestUtils.setField(startupRunner, "workflowEnabled", false);

        startupRunner.run();

        verify(webhookService, never()).executeWorkflow();
    }
}
