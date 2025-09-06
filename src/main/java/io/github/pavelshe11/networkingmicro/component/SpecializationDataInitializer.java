package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.enums.SpecializationLevelType;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class SpecializationDataInitializer implements ApplicationRunner {
    private final SpecializationRepository specializationRepository;
    Logger log = LoggerFactory.getLogger(SpecializationDataInitializer.class);

    @Override
    public void run(ApplicationArguments args) {
//
//        String specializationName = "ИТ";
//        String specializationName2 = "ИБ";
//        String specializationName3 = "ИИ";
//
//        boolean specializationExists = specializationRepository.existsByName(specializationName);
//        boolean specializationExists2 = specializationRepository.existsByName(specializationName2);
//        boolean specializationExists3 = specializationRepository.existsByName(specializationName3);
//        if (!specializationExists) {
//            SpecializationEntity specialization = SpecializationEntity.builder()
//                    .name(specializationName)
////                    .countOfCourses(3)
//                    .build();
//            specializationRepository.save(specialization);
//        }
//
//        if (!specializationExists2) {
//            SpecializationEntity specialization = SpecializationEntity.builder()
//                    .name(specializationName2)
////                    .countOfCourses(4)
//                    .build();
//            specializationRepository.save(specialization);
//        }
//
//        if (!specializationExists3) {
//            SpecializationEntity specialization = SpecializationEntity.builder()
//                    .name(specializationName3)
////                    .countOfCourses(5)
//                    .build();
//            specializationRepository.save(specialization);
//        }

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
}
