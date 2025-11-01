package io.github.fantasmadux.usermicro.component.job;

import io.github.fantasmadux.usermicro.SpringContext;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class SpecializationCleanJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(SpecializationCleanJob.class);


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            SpecializationCleanExecution executor = SpringContext.getBean(SpecializationCleanExecution.class);
            executor.actualizeInstitutionSpecialties();
        } catch (Exception e) {
            log.error("Ошибка очистки специализаций", e);
            throw new JobExecutionException(e);
        }
    }
}
