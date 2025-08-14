package io.github.pavelshe11.networkingmicro.api.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmailUpdateResponseDto {
    private String codePattern;
    private long codeExpires;
}
