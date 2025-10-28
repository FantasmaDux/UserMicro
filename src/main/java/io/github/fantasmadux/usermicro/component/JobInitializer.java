package io.github.fantasmadux.usermicro.component;

import io.github.fantasmadux.usermicro.services.SpecializationCleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobInitializer {
    private final SpecializationCleanerService specializationCleanerService;

    @Value("${ACTUALIZE_INSTITUTION_SPECIALITIES_TIME}")
    private String actualizeSpecializationTime;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void onApplicationReady() {
        specializationCleanerService.scheduledSpecializationCleaner(actualizeSpecializationTime);
    }
}
