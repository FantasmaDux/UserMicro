package io.github.pavelshe11.networkingmicro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {
    private String field;
    private String message;
}
