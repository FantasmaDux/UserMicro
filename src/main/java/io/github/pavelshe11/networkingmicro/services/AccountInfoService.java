package io.github.pavelshe11.networkingmicro.services;

import com.google.protobuf.Value;
import io.github.pavelshe11.networkingmicro.api.dto.responses.AccountInfoDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.GetAvatarResponseDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.AccountNotFoundException;
import io.github.pavelshe11.networkingmicro.api.exceptions.AvatarNotFoundException;
import io.github.pavelshe11.networkingmicro.grpc.getAccountInfoProto;
import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.ActivitySessionEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountContactInfoRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.ActivitySessionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountInfoService {
    private static final Logger log = LoggerFactory.getLogger(AccountInfoService.class);
    private final AccountRepository accountRepository;
    private final AccountContactInfoRepository accountContactInfoRepository;
    private final ActivitySessionRepository activitySessionRepository;

    @Transactional
    public getAccountInfoProto.GetAccountInfoResponse.Builder getAccountInfoByEmail(String email) {

        Optional<AccountEntity> accountOpt = accountRepository.findByMainEmailContactContact(email);

        return getBuilderResponse(accountOpt);
    }

    @Transactional
    public getAccountInfoProto.GetAccountInfoResponse.Builder getAccountById(String accountId) {

        Optional<AccountEntity> accountOpt = accountRepository.findById(UUID.fromString(accountId));

        return getBuilderResponse(accountOpt);
    }

    private getAccountInfoProto.GetAccountInfoResponse.Builder getBuilderResponse(Optional<AccountEntity> accountOpt) {
        getAccountInfoProto.GetAccountInfoResponse.Builder responseBuild = getAccountInfoProto.GetAccountInfoResponse.newBuilder();

        if (accountOpt.isEmpty()) {
            return responseBuild;
        }

        AccountEntity account = accountOpt.get();
        Map<String, Value> userData = new HashMap<>();

        userData.put("account_id", Value.newBuilder().setStringValue(account.getId().toString()).build());
        userData.put("role", Value.newBuilder().setStringValue(account.isAdmin() ? "admin" : "user").build());
        userData.put("ip", Value.newBuilder().setStringValue(Optional.ofNullable(account.getIp()).orElse("")).build());

        responseBuild.putAllUserData(userData);

        return responseBuild;
    }

    public AccountInfoDto getAccountFullInfo(UUID accountId) {

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new AccountNotFoundException();
        }

        AccountEntity account = accountOpt.get();

        Optional<ActivitySessionEntity> activitySessionOpt = activitySessionRepository.findByAccount(account);

        ActivitySessionEntity inActivitySession = activitySessionOpt.get();

        String email = accountContactInfoRepository
                .findById(account.getMainEmailContact().getId())
                .map(AccountContactInfoEntity::getContact)
                .orElse(null);

        AccountInfoDto accountInfoDto = AccountInfoDto.builder()
                .firstName(nullIfBlank(account.getFirstName()))
                .lastName(nullIfBlank(account.getLastName()))
                .middleName(nullIfBlank(account.getMiddleName()))
                .bio(nullIfBlank(account.getBio()))
                .email(email)
                .professor(account.isProfessor())
                .networking(account.isNetworking())
                .cityVisible(account.getCityVisible())
                .dateOfBirthVisible(account.getDateOfBirthVisible())
                .consulting(account.isConsulting())
                .dateOfEducationStart(account.getDateOfEducationStart() != null ?
                        account.getDateOfEducationStart() : null)
                .dateOfEducationEnd(account.getDateOfEducationEnd() != null ?
                        account.getDateOfEducationEnd() : null)
                .dateOfBirth(account.getDateOfBirth() != null ? account.getDateOfBirth() : null)
                .inactivityTimeMs(inActivitySession.getInactivityTimeMs())
                .cityName(account.getCity() != null ? account.getCity().getName() : null)
                .specializationName(account.getSpecialization() != null ? account.getSpecialization().getName() : null)
                .educationalInstitutionName(account.getEducationalInstitution() != null ? account.getEducationalInstitution().getName() : null)
                .educationalInstitutionId(account.getEducationalInstitution() != null ? account.getEducationalInstitution().getId() : null)
                .specializationId(account.getSpecialization() != null ? account.getSpecialization().getId() : null)

                .build();

        return accountInfoDto;
    }

    public GetAvatarResponseDto getAvatar(UUID accountId) {

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new AccountNotFoundException();
        }
        AccountEntity account = accountOpt.get();

        byte[] avatar = account.getAvatar();
        if (avatar == null) {
            log.error("Аватара нет.");
            throw new AvatarNotFoundException();
        }

        String mimeType = account.getAvatarMimeType() != null
                ? account.getAvatarMimeType().getMimetype()
                : "application/octet-stream";

        return new GetAvatarResponseDto(avatar, mimeType);
    }

    private String nullIfBlank(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}
