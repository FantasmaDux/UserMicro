package io.github.pavelshe11.networkingmicro.api.dto.requests;

import io.github.pavelshe11.networkingmicro.api.dto.AccountContactInfoDto;
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
@Schema(description = "Запрос на добавление или изменение контактной информации пользователя")
public class ContactInfoUpdateRequestDto {
    private List<AccountContactInfoDto> accountContactMethods;
}
