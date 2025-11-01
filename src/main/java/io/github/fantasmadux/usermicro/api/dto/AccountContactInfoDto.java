package io.github.fantasmadux.usermicro.api.dto;

import io.github.fantasmadux.usermicro.store.entities.AccountContactInfoEntity;
import io.github.fantasmadux.usermicro.store.enums.ContactMethodType;
import io.github.fantasmadux.usermicro.store.enums.VisibilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Основа для запросов и ответов по контактной информации")
public class AccountContactInfoDto {
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

    @NotBlank
    @Schema(
            description = "ID контакта"
    )
    private UUID contactId;

    @Schema(
            description = "Ссылка на иконку сайта, связанного с контактом",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "https://github.com/favicon.ico"
    )
    private String iconUrl;

    @Schema(
            description = "Определяет, виден ли этот контакт остальным пользователям." +
                    "Возможные значения: PUBLIC, PRIVATE",
            example = "PUBLIC"
    )
    private VisibilityType visibility;

    @Schema(
            description = "Определяет, разрешено ли изменять контакт." +
                    "Возможные значения: true, false",
            example = "true"
    )
    private Boolean modifiable;

    public static AccountContactInfoDto fromEntity(AccountContactInfoEntity entity) {
        return AccountContactInfoDto.builder()
                .contactMethodType(entity.getContactMethod())
                .contact(entity.getContact())
                .iconUrl(entity.getIconUrl())
                .visibility(entity.getVisibility())
                .contactId(entity.getId())
                .modifiable(entity.isModifiable())
                .build();
    }
}
