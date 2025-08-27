package io.github.pavelshe11.networkingmicro.api.dto;

import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Основа для запросов и ответов по контактной информации")
public class AccountContactInfoDto {
    @Schema(
            description = "Тип контакта. Возможные значения: LINK, PHONE, EMAIL",
            example = "LINK"
    )
    private ContactMethodType contactMethodType;

    @Schema(
            description = "Контакт: ссылка, телефон или email",
            example = "https://github.com/username"
    )
    private String contact;

    @Schema(
            description = "Ссылка на иконку сайта, связанного с контактом",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "https://github.com/favicon.ico"
    )
    private String iconUrl;

    @Schema(
            description = "Определяет, виден ли этот контакт остальным пользователям",
            example = "true"
    )
    private boolean visibility;

    public static AccountContactInfoDto fromEntity(AccountContactInfoEntity entity) {
        return AccountContactInfoDto.builder()
                .contactMethodType(entity.getContactMethod())
                .contact(entity.getContact())
                .iconUrl(entity.getIconUrl())
                .build();
    }
}
