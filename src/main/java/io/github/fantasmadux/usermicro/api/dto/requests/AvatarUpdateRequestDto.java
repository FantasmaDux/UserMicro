package io.github.fantasmadux.usermicro.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на онбовление аватара")
public class AvatarUpdateRequestDto {
    @Schema(description = "Аватар пользователя в виде файла")
    private MultipartFile avatar;

}
