package io.github.pavelshe11.networkingmicro.validators;

import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountValidator {
    private final EducationalInstitutionRepository institutionRepository;
    private final MessageSource messageSource;


}
