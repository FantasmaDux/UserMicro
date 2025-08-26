package io.github.pavelshe11.networkingmicro.api.dto;

import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountContactInfoDto {
    private ContactMethodType contactMethodType;
    private String contact;
    private String faviconUrl;
}
