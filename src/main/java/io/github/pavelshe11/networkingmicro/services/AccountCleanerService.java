package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.component.job.AccountCleanJob;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountCleanerService {
    private static final Logger log = LoggerFactory.getLogger(AccountCleanerService.class);
    private final Scheduler scheduler;

    public void cleanInactiveAccounts(UUID accountId, long triggerTimeMs) {
        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("accountId", accountId.toString());

            JobDetail jobDetail = JobBuilder.newJob(AccountCleanJob.class)
                    .withIdentity("trigger-cleanup-" + accountId, "account-jobs")
                    .withDescription("delete inactive account")
                    .usingJobData(jobDataMap)
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobDetail.getKey().getName(), "account-jobs")
                    .withDescription("delete inactive account trigger")
                    .forJob(jobDetail)
                    .startAt(new Date(triggerTimeMs))
                    .build();

            scheduler.deleteJob(jobDetail.getKey());
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("Запланировано удаление аккаунта {} на {}", accountId, new Date(triggerTimeMs));
        } catch (Exception e) {
            log.error("Ошибка при планировании задачи на удаление аккаунта {}", accountId, e);
            throw new ServerAnswerException();
        }
    }
}
