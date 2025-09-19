package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.component.job.SpecializationCleanJob;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpecializationCleanerService {
    private static final Logger log = LoggerFactory.getLogger(SpecializationCleanerService.class);
    private final Scheduler scheduler;

    public void scheduledSpecializationCleaner(String actualizeSpecializationTime) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(SpecializationCleanJob.class)
                    .withIdentity("specialization-clean-job", "specialization-jobs")
                    .withDescription("refresh specialization information" +
                            " from InstitutionSpecialtiesEntity")
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("specialization-clean-trigger", "specialization-jobs")
                    .withSchedule(CronScheduleBuilder.cronSchedule(actualizeSpecializationTime))
                    .forJob(jobDetail)
                    .build();

            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.addJob(jobDetail, true);
                scheduler.scheduleJob(trigger);
            } else {
                log.info("Job уже существует");
            }

            log.info("Задача по очистке специализаций запланирована");
        } catch (Exception e) {
            log.error("Ошибка при планировании задачи на обновление специализаций", e);
            throw new ServerAnswerException();
        }
    }
}

