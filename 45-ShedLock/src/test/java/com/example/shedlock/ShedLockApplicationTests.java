package com.example.shedlock;

import com.example.shedlock.scheduler.CleanupScheduler;
import com.example.shedlock.scheduler.ReportScheduler;
import com.example.shedlock.service.ProgrammaticJobRunner;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShedLockApplicationTests {

    @Autowired
    private LockProvider lockProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProgrammaticJobRunner programmaticJobRunner;

    @Autowired
    private ReportScheduler reportScheduler;

    @Autowired
    private CleanupScheduler cleanupScheduler;

    @Test
    void contextLoads() {
        assertThat(lockProvider).isNotNull();
    }

    @Test
    void testLockTableExists() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM shedlock", Integer.class);
        assertThat(count).isNotNull();
    }

    @Test
    void testProgrammaticLockSuccess() {
        boolean executed = programmaticJobRunner.runJobProgrammatically();
        assertThat(executed).isTrue();
    }

    @Test
    void testProgrammaticLockPreventsDuplicate() {
        // Acquire lock manually
        LockConfiguration config = new LockConfiguration(
                Instant.now(),
                "ProgrammaticJobRunner_runJob",
                Duration.ofSeconds(10),
                Duration.ofSeconds(5)
        );
        Optional<SimpleLock> lock = lockProvider.lock(config);
        assertThat(lock).isPresent();

        // Try to run job programmatic (should fail because lock is held)
        boolean executed = programmaticJobRunner.runJobProgrammatically();
        assertThat(executed).isFalse();

        // Release lock
        lock.get().unlock();
    }

    @Test
    void testSchedulersAreRegistered() {
        assertThat(reportScheduler).isNotNull();
        assertThat(cleanupScheduler).isNotNull();
    }
}
