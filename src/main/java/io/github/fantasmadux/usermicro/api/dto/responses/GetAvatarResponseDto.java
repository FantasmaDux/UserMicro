package io.github.fantasmadux.usermicro.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Ответ на запрос получения аватара")
public class GetAvatarResponseDto {
    @Schema(description = "Аватар в байтах")
    private final byte[] avatar;
    @Schema(description = "Тип media")
    private final String avatarMimeType;
}
