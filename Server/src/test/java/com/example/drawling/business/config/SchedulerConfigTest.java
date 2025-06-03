package com.example.drawling.business.config;

import com.example.drawling.config.SchedulerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigTest {
    @Mock
    private ScheduledTaskRegistrar mockTaskRegistrar;

    @MockBean
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private SchedulerConfig schedulerConfig;


    @BeforeEach
    void setUp() {
        schedulerConfig = new SchedulerConfig();
    }

    @Test
    void testTaskSchedulerConfiguration() {
        threadPoolTaskScheduler = (ThreadPoolTaskScheduler) schedulerConfig.taskScheduler();

        assertNotNull(threadPoolTaskScheduler, "TaskScheduler bean should not be null");
        assertEquals(0, threadPoolTaskScheduler.getPoolSize(), "Pool size should be set to 0");
        assertEquals("scheduled-task-", threadPoolTaskScheduler.getThreadNamePrefix(), "Thread name prefix should be 'scheduled-task-'");
    }



    @Test
    void testConfigureTasks() {
        // Arrange
        TaskScheduler mockTaskScheduler = mock(TaskScheduler.class);
        SchedulerConfig spyConfig = spy(schedulerConfig);
        doReturn(mockTaskScheduler).when(spyConfig).taskScheduler();

        // Act
        spyConfig.configureTasks(mockTaskRegistrar);

        // Assert
        verify(mockTaskRegistrar, times(1)).setTaskScheduler(mockTaskScheduler);
    }
}
