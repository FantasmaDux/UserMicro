package io.github.fantasmadux.usermicro.store.enums;

import io.github.fantasmadux.usermicro.normalization.DataNormalisation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SpecializationLevelType {

    PROFESSIONAL_SPO(
            "Раздел 1. Профессии среднего профессионального образования",
            "СПО ПРОФЕССИИ"
    ),

    SPECIALIZATION_SPO(
            "Раздел 2. Специальности среднего профессионального образования",
            "СПО СПЕЦИАЛЬНОСТИ"
    ),

    BACHELOR(
            "Раздел 3. Направления подготовки высшего образования - бакалавриата",
            "БАКАЛАВРИАТ"
    ),

    MASTER(
            "Раздел 4. Направления подготовки высшего образования - магистратуры",
            "МАГИСТРАТУРА"
    ),

    SPECIALIST(
            "Раздел 5. Специальности высшего образования - специалитета",
            "СПЕЦИАЛИТЕТ"
    ),

    ASPIRANTURA(
            "Раздел 6. Направления подготовки высшего образования -" +
                    " подготовки кадров высшей квалификации" +
                    " по программам подготовки научно-педагогических кадров в аспирантуре",
            "АСПИРАНТУРА"
    ),

    ADJUNCTURA(
            "Раздел 7. Направления подготовки высшего образования - " +
                    "подготовки кадров высшей квалификации по программам подготовки научно-педагогических " +
                    "кадров в адъюнктуре",
            "АДЪЮНКТУРА"
    ),

    RESIDENCY(
            "Раздел 8. Специальности высшего образования - " +
                    "подготовки кадров высшей квалификации по программам ординатуры",
            "ОРДИНАТУРА"
    ),

    ASSISTANTSHIP(
            "Раздел 9. Специальности высшего образования -" +
                    " подготовки кадров высшей квалификации по программам ассистентуры-стажировки",
            "АССИСТЕНТУРА-СТАЖИРОВКА"
    );

    private final String specializationLevelLongName;
    private final String specializationLevelShortName;

    public static SpecializationLevelType findByShortName(String shortName) {
        if (shortName == null || shortName.isBlank()) {
            return null;
        }

        String normalizedShortName = DataNormalisation.normalizeTextForComparison(shortName);

        for (SpecializationLevelType type : SpecializationLevelType.values()) {
            String normalizedEnumName =
                    DataNormalisation.normalizeTextForComparison(type.getSpecializationLevelShortName());

            if (normalizedEnumName.contains(normalizedShortName) ||
            normalizedShortName.contains(normalizedEnumName)) {
                return type;
            }
        }

        return null;
    }

}
