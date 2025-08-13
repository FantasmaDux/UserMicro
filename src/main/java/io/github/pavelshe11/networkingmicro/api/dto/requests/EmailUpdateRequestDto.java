package io.github.pavelshe11.networkingmicro.api.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class EmailUpdateRequestDto {
    private String email;
}
