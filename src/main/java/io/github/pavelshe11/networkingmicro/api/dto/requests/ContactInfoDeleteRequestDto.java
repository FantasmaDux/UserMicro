package io.github.pavelshe11.networkingmicro.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на удаление контактной информации пользователя")
public class ContactInfoDeleteRequestDto {
    List<UUID> contactMethodsIds;
}
