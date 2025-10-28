package io.github.fantasmadux.usermicro.component.job;

import io.github.fantasmadux.usermicro.SpringContext;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@DisallowConcurrentExecution
public class AccountCleanJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(AccountCleanJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String accountIdStr = context.getMergedJobDataMap().getString("accountId");
        UUID accountId = UUID.fromString(accountIdStr);

        try {
            AccountCleanExecution executor = SpringContext.getBean(AccountCleanExecution.class);
            executor.clean(accountId);
        } catch (Exception e) {
            log.error("Ошибка удаления аккаунта {}", accountId, e);
            throw new JobExecutionException(e);
        }
    }
}
