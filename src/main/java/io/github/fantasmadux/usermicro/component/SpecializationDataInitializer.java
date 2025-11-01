package io.github.fantasmadux.usermicro.component;

import io.github.fantasmadux.usermicro.store.entities.SpecializationEntity;
import io.github.fantasmadux.usermicro.store.enums.SpecializationLevelType;
import io.github.fantasmadux.usermicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Order(1)
public class SpecializationDataInitializer implements ApplicationRunner {
    private final SpecializationRepository specializationRepository;
    Logger log = LoggerFactory.getLogger(SpecializationDataInitializer.class);

    @Override
    public void run(ApplicationArguments args) {

        try {

            ClassPathResource resource = new ClassPathResource("data/specializationData.xlsx");
            InputStream inputStream = resource.getInputStream();

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            SpecializationLevelType currentSpecializationLevel = null;

            for (Row row : sheet) {
                Cell codeCell = row.getCell(0);
                Cell nameCell = row.getCell(1);

                String code = (codeCell != null && codeCell.getCellType() == CellType.STRING)
                        ? codeCell.getStringCellValue().trim()
                        : null;
                String name = (nameCell != null && nameCell.getCellType() == CellType.STRING)
                        ? nameCell.getStringCellValue().trim()
                        : null;

                if (code == null || code.isBlank()) {
                    if (name != null && name.trim().toLowerCase().contains("раздел")) {
                        currentSpecializationLevel =
                                SpecializationLevelType.findByShortName(name.trim());
                    }
                    continue;
                }

                if (!isValidCode(code)) {
                    continue;
                }

                if (name != null && !name.isBlank() && currentSpecializationLevel != null) {
                    if (!specializationRepository.existsByName(name)) {
                        SpecializationEntity entity = SpecializationEntity.builder()
                                .specializationCode(code.trim())
                                .cleanCode(extractCodeClean(code.trim()))
                                .name(name)
                                .specializationLevel(currentSpecializationLevel)
                                .build();

                        specializationRepository.save(entity);
                    }
                }
            }

            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            log.error("Исключение при вынесении в бд {}", String.valueOf(e));
        }
    }

    // для пропуска подразделов специальностей
    private boolean isValidCode(String code) {
        return code != null && code.matches("^\\d{1,2}(\\.\\d{2}){3}$");
    }

    // отсечение раздела в коде
    private String extractCodeClean(String code) {
        if (code == null) return null;
        int dotIndex = code.indexOf('.');
        if (dotIndex == -1 || dotIndex == code.length() - 1) return code;
        return code.substring(dotIndex + 1);
    }
}
