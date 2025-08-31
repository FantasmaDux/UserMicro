package io.github.pavelshe11.networkingmicro.api.dto.requests;

import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import io.github.pavelshe11.networkingmicro.store.enums.ContactVisibilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "Запрос на добавление или изменение контактной информации пользователя",
        example = """
                    {
                      "contactMethodType": "LINK",
                      "contact": "https://github.com/username",
                      "visibility": "PRIVATE"
                    }
                """
)
public class ContactInfoAddRequestDto {
    @NotNull
    @Schema(
            description = "Тип контакта. Возможные значения: LINK, PHONE, EMAIL",
            example = "LINK"
    )
    private ContactMethodType contactMethodType;

    @NotBlank
    @Schema(
            description = "Контакт: ссылка, телефон или email",
            example = "https://github.com/username"
    )
    private String contact;

    @Schema(
            description = "Определяет, виден ли этот контакт остальным пользователям." +
                    "Возможные значения: PUBLIC, PRIVATE",
            example = "PUBLIC"
    )
    private ContactVisibilityType visibility;
}
