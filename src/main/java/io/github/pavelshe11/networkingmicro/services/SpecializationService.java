package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationPageDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationsByInstitutionDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationsDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.InstitutionNotFoundException;
import io.github.pavelshe11.networkingmicro.api.exceptions.SpecializationNotFoundException;
import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.InstitutionSpecialtiesEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.InstitutionSpecialtiesRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final SpecializationRepository specializationRepository;
    private final InstitutionSpecialtiesRepository institutionSpecialtiesRepository;

    private enum SearchType {CODE, NAME, MIXED, GENERAL}

    @Transactional
    public SpecializationsByInstitutionDto getSpecializationsByInstitution(UUID institutionId) {

        if (!educationalInstitutionRepository.existsById(institutionId)) {
            log.error("Институт с id {} не найден", institutionId);
            throw new InstitutionNotFoundException();
        }

        Optional<EducationalInstitutionEntity> educationalInstitutionOpt
                = educationalInstitutionRepository.findById(institutionId);

        EducationalInstitutionEntity educationalInstitution = educationalInstitutionOpt.get();

        List<SpecializationsDto> specializations =
                educationalInstitution.getInstitutionSpecialties().stream()
                        .map(InstitutionSpecialtiesEntity::getSpecialization)
                        .map(specialization ->
                        {
                            String fullCode = specialization.getSpecializationCode();
                            String trimmedCode = fullCode.contains(".")
                                    ? fullCode.substring(fullCode.indexOf('.') + 1)
                                    : fullCode;

                            return SpecializationsDto.builder()
                                    .id(specialization.getId())
                                    .code(trimmedCode)
                                    .name(specialization.getName())
                                    .build();
                        })
                        .toList();

        return SpecializationsByInstitutionDto.builder()
                .specializationsByInstitution(specializations)
                .build();
    }

    public SpecializationPageDto getSpecializations(UUID institutionId,
                                                    String keyword,
                                                    String cursor, int size) {
        String cursorName = null;
        UUID cursorId = null;

        if (cursor != null && !cursor.isEmpty()) {
            String decoded = new String(Base64.getDecoder().decode(cursor));
            String[] parts = decoded.split("\\|", 2);
            cursorName = parts[0];
            cursorId = UUID.fromString(parts[1]);
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
                size + 1
        );

        List<SpecializationsDto> content = rows.stream()
                .map(row -> new SpecializationsDto(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2]
                ))
                .collect(Collectors.toList());

        return SpecializationPageDto.ofByNameAndId(content, size);
    }

    private List<Object[]> processSearch(UUID institutionId,
                                         String normalizedKeyword,
                                         String cursorName,
                                         UUID cursorId,
                                         SearchType searchType,
                                         boolean hasInstitution,
                                         int limit) {
        String searchPattern = "%" + normalizedKeyword.replace(" ", "%") + "%";

        return switch (searchType) {
            case CODE -> processCodeSearch(institutionId, searchPattern, cursorName, cursorId, hasInstitution, limit);
            case NAME -> processNameSearch(institutionId, searchPattern, cursorName, cursorId, hasInstitution, limit);
            case MIXED ->
                    processMixedSearch(institutionId, normalizedKeyword, cursorName, cursorId, hasInstitution, limit);
            case GENERAL -> processGeneralSearch(cursorName, cursorId, limit);
        };
    }

    private List<Object[]> processGeneralSearch(String cursorName, UUID cursorId, int limit) {
        return specializationRepository.findAllSpecializationsWithKeysetPagination(
                cursorName,
                cursorId,
                limit
        );
    }

    private List<Object[]> processMixedSearch(UUID institutionId, String normalizedKeyword, String cursorName, UUID cursorId, boolean hasInstitution, int limit) {
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
        if (hasInstitution) {
            List<Object[]> rows = specializationRepository.findByInstitutionWithCodeAndName(
                    institutionId,
                    codeSearchPattern,
                    nameSearchPattern,
                    cursorName,
                    cursorId,
                    limit
            );
            if (rows.isEmpty()) {
                return specializationRepository.findByCodeAndNameWithoutInstitution(
                        codeSearchPattern,
                        nameSearchPattern,
                        cursorName,
                        cursorId,
                        limit
                );
            }
            return rows;
        } else {
            return specializationRepository.findByCodeAndNameWithoutInstitution(
                    codeSearchPattern,
                    nameSearchPattern,
                    cursorName,
                    cursorId,
                    limit
            );
        }
    }

    private List<Object[]> processNameSearch(UUID institutionId, String searchPattern, String cursorName, UUID cursorId, boolean hasInstitution, int limit) {
        if (hasInstitution) {
            List<Object[]> rows = specializationRepository.findByInstitutionWithName(
                    institutionId,
                    searchPattern,
                    cursorName,
                    cursorId,
                    limit
            );
            if (rows.isEmpty()) {
                return specializationRepository.findByNameWithoutInstitution(
                        searchPattern,
                        cursorName,
                        cursorId,
                        limit
                );
            }
            return rows;
        } else {
            return specializationRepository.findByNameWithoutInstitution(
                    searchPattern,
                    cursorName,
                    cursorId,
                    limit
            );
        }
    }

    private List<Object[]> processCodeSearch(UUID institutionId, String searchPattern, String cursorName, UUID cursorId, boolean hasInstitution, int limit) {
        if (hasInstitution) {
            List<Object[]> rows = specializationRepository.findByInstitutionWithCode(
                    institutionId,
                    searchPattern,
                    cursorName,
                    cursorId,
                    limit
            );
            if (rows.isEmpty()) {
                return specializationRepository.findByCodeWithoutInstitution(
                        searchPattern,
                        cursorName,
                        cursorId,
                        limit
                );
            }
            return rows;
        } else {
            return specializationRepository.findByCodeWithoutInstitution(
                    searchPattern,
                    cursorName,
                    cursorId,
                    limit
            );
        }
    }


    private static SearchType getSearchType(String keyword) {
        boolean hasDigit = keyword.matches(".*\\d.*");
        boolean hasLetter = keyword.matches(".*[а-яА-Яa-zA-Z].*");

        if (hasDigit && !hasLetter) return SearchType.CODE;
        if (hasLetter && !hasDigit) return SearchType.NAME;
        if (hasDigit && hasLetter) return SearchType.MIXED;
        return SearchType.GENERAL;
    }

    public void createSpecializationInstitutionRelation(UUID educational_institution_id,
                                                        UUID specialization_id) {

        Optional<EducationalInstitutionEntity> educationalInstitution =
                educationalInstitutionRepository.findById(educational_institution_id);

        if (educationalInstitution.isEmpty()) {
            throw new InstitutionNotFoundException();
        }

        Optional<SpecializationEntity> specialization =
                specializationRepository.findById(specialization_id);

        if (specialization.isEmpty()) {
            throw new SpecializationNotFoundException();
        }

        InstitutionSpecialtiesEntity institutionSpecialties = InstitutionSpecialtiesEntity.builder()
                .educationalInstitution(educationalInstitution.get())
                .specialization(specialization.get())
                .build();

        institutionSpecialtiesRepository.save(institutionSpecialties);
    }
}
