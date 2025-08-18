package io.github.pavelshe11.networkingmicro.api.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@Builder
public class AvatarUpdateRequestDto {
    private MultipartFile avatar;

}
