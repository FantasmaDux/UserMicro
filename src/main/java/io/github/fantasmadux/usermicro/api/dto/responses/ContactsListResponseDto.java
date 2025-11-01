package io.github.fantasmadux.usermicro.api.dto.responses;

import io.github.fantasmadux.usermicro.api.dto.AccountContactInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на получение контактной информации по ID аккаунта")
public class ContactsListResponseDto {
    @Schema(description = "Список контактов пользователя")
    private List<AccountContactInfoDto> contactMethods;
}
