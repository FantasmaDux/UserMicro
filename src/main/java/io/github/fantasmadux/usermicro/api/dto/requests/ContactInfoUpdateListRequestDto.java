package io.github.fantasmadux.usermicro.api.dto.requests;

import io.github.fantasmadux.usermicro.store.enums.ContactMethodType;
import io.github.fantasmadux.usermicro.store.enums.VisibilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на редактирование контактной информации с указанием ID контакта")
public class ContactInfoUpdateListRequestDto {

    @NotNull
    @Schema(description = "Список контактных данных для обновления")
    @Valid
    private List<ContactInfoUpdateRequestDto> accountContactMethods;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Элемент данных для обновления контакта")
    public static class ContactInfoUpdateRequestDto {

        @NotNull
        @Schema(
                description = "ID контакта для обновления",
                example = "b68d67e7-63f9-4a53-bbde-bd1319a82529"
        )
        private UUID contactId;

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
                description = "Определяет, виден ли этот контакт остальным пользователям." +
                        "Возможные значения: PUBLIC, PRIVATE",
                example = "PUBLIC"
        )
        private VisibilityType visibility;
    }
}
