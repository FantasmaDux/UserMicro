package io.github.fantasmadux.usermicro.services;

import io.github.fantasmadux.usermicro.api.dto.responses.SpecializationPageDto;
import io.github.fantasmadux.usermicro.api.dto.responses.SpecializationsDto;
import io.github.fantasmadux.usermicro.api.exceptions.ServerAnswerException;
import io.github.fantasmadux.usermicro.store.enums.CursorDestinationType;
import io.github.fantasmadux.usermicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    Logger log = LoggerFactory.getLogger(SpecializationService.class);
    private final SpecializationRepository specializationRepository;

    private enum SearchType {CODE, NAME, MIXED, GENERAL}

    public SpecializationPageDto getSpecializations(UUID institutionId,
                                                    String keyword,
                                                    String cursor,
                                                    int size,
                                                    CursorDestinationType cursorDestinationProvidedType) {

        CursorDestinationType cursorDestinationType = Optional
                .ofNullable(cursorDestinationProvidedType)
                .orElse(CursorDestinationType.AFTER);

        String cursorName = null;
        UUID cursorId = null;

        if (cursor != null && !cursor.isEmpty()) {
            try {

                log.info("Получен cursor: [{}]", cursor);
                String decoded = new String(Base64.getDecoder().decode(cursor));
                log.info("Декодированный cursor: [{}]", decoded);
                String[] parts = decoded.split("\\|", 2);
                cursorName = parts[0];
                cursorId = UUID.fromString(parts[1]);
                log.info("Парсинг курсора прошёл успешно. cursorName: [{}], cursorId: [{}]", cursorName, cursorId);
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                log.warn("Ошибка при разборе параметра cursor: [{}]", cursor, e);
                throw new ServerAnswerException();
            }
        }

        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        normalizedKeyword = normalizedKeyword.replaceAll("\\s+", " ");

        SearchType searchType = getSearchType(normalizedKeyword);
        boolean hasInstitution = institutionId != null;

        List<Object[]> rows = processSearch(
                institutionId,
                normalizedKeyword,
                cursorName,
                cursorId,
                searchType,
                hasInstitution,
                size + 1,
                cursorDestinationType
        );

        List<SpecializationsDto> content = rows.stream()
                .map(row -> new SpecializationsDto(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2]
                ))
                .collect(Collectors.toList());

        return SpecializationPageDto.ofByNameAndId(content, size, cursorDestinationType, cursorName, cursorId);
    }

    private List<Object[]> processSearch(UUID institutionId,
                                         String normalizedKeyword,
                                         String cursorName,
                                         UUID cursorId,
                                         SearchType searchType,
                                         boolean hasInstitution,
                                         int limit,
                                         CursorDestinationType cursorDestinationType) {
        String searchPattern = "%" + normalizedKeyword.replace(" ", "%") + "%";

        return switch (searchType) {
            case CODE ->
                    processCodeSearch(institutionId, searchPattern, cursorName, cursorId, hasInstitution, limit, cursorDestinationType);
            case NAME ->
                    processNameSearch(institutionId, searchPattern, cursorName, cursorId, hasInstitution, limit, cursorDestinationType);
            case MIXED ->
                    processMixedSearch(institutionId, normalizedKeyword, cursorName, cursorId, hasInstitution, limit, cursorDestinationType);
            case GENERAL -> processGeneralSearch(cursorName, cursorId, limit, cursorDestinationType);
        };
    }

    private List<Object[]> processGeneralSearch(String cursorName, UUID cursorId, int limit, CursorDestinationType cursorDestinationType) {
        if (cursorDestinationType.isAfter()) {
            return specializationRepository.findAllSpecializationsWithKeysetPaginationAfter(
                    cursorName,
                    cursorId,
                    limit
            );
        } else {
            return specializationRepository.findAllSpecializationsWithKeysetPaginationBefore(
                    cursorName,
                    cursorId,
                    limit
            );
        }
    }

    private List<Object[]> processMixedSearch(UUID institutionId, String normalizedKeyword, String cursorName, UUID cursorId, boolean hasInstitution, int limit, CursorDestinationType cursorDestinationType) {
        String codePart = "";
        String namePart = normalizedKeyword;

        Pattern codePatternRegex = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{2}");
        Matcher matcher = codePatternRegex.matcher(normalizedKeyword);

        if (matcher.find()) {
            codePart = matcher.group();
            namePart = normalizedKeyword.replace(codePart, "").trim();
        }

        String codeSearchPattern = codePart;
        String nameSearchPattern = "%" + namePart.replace(" ", "%") + "%";

        boolean isAfter = cursorDestinationType.isAfter();

        List<Object[]> rows;
        if (hasInstitution) {
            rows = isAfter
                    ? specializationRepository.findByInstitutionWithCodeAndNameAfter(
                    institutionId, codeSearchPattern, nameSearchPattern, cursorName, cursorId, limit)
                    : specializationRepository.findByInstitutionWithCodeAndNameBefore(
                    institutionId, codeSearchPattern, nameSearchPattern, cursorName, cursorId, limit);
            if (!rows.isEmpty()) return rows;
        }

        return isAfter
                ? specializationRepository.findByCodeAndNameWithoutInstitutionAfter(
                codeSearchPattern, nameSearchPattern, cursorName, cursorId, limit)
                : specializationRepository.findByCodeAndNameWithoutInstitutionBefore(
                codeSearchPattern, nameSearchPattern, cursorName, cursorId, limit);
    }

    private List<Object[]> processNameSearch(UUID institutionId, String searchPattern, String cursorName, UUID cursorId, boolean hasInstitution, int limit, CursorDestinationType cursorDestinationType) {
        boolean isAfter = cursorDestinationType.isAfter();

        List<Object[]> rows;
        if (hasInstitution) {
            rows = isAfter
                    ? specializationRepository.findByInstitutionWithNameAfter(
                    institutionId, searchPattern, cursorName, cursorId, limit)
                    : specializationRepository.findByInstitutionWithNameBefore(
                    institutionId, searchPattern, cursorName, cursorId, limit);
            if (!rows.isEmpty()) return rows;
        }

        return isAfter
                ? specializationRepository.findByNameWithoutInstitutionAfter(
                searchPattern, cursorName, cursorId, limit)
                : specializationRepository.findByNameWithoutInstitutionBefore(
                searchPattern, cursorName, cursorId, limit);
    }

    private List<Object[]> processCodeSearch(UUID institutionId, String searchPattern, String cursorName, UUID cursorId, boolean hasInstitution, int limit, CursorDestinationType cursorDestinationType) {
        boolean isAfter = cursorDestinationType.isAfter();

        List<Object[]> rows;
        if (hasInstitution) {
            rows = isAfter
                    ? specializationRepository.findByInstitutionWithCodeAfter(
                    institutionId, searchPattern, cursorName, cursorId, limit)
                    : specializationRepository.findByInstitutionWithCodeBefore(
                    institutionId, searchPattern, cursorName, cursorId, limit);
            if (!rows.isEmpty()) return rows;
        }

        return isAfter
                ? specializationRepository.findByCodeWithoutInstitutionAfter(
                searchPattern, cursorName, cursorId, limit)
                : specializationRepository.findByCodeWithoutInstitutionBefore(
                searchPattern, cursorName, cursorId, limit);
    }


    private static SearchType getSearchType(String keyword) {
        boolean hasDigit = keyword.matches(".*\\d.*");
        boolean hasLetter = keyword.matches(".*[а-яА-Яa-zA-Z].*");

        if (hasDigit && !hasLetter) return SearchType.CODE;
        if (hasLetter && !hasDigit) return SearchType.NAME;
        if (hasDigit && hasLetter) return SearchType.MIXED;
        return SearchType.GENERAL;
    }
}