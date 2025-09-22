package io.github.pavelshe11.networkingmicro.api.dto.responses;

import io.github.pavelshe11.networkingmicro.store.enums.CursorDestinationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Ответ на запрос получения специализаций")
public class SpecializationPageDto extends PageDto<SpecializationsDto> {

    public SpecializationPageDto(List<SpecializationsDto> content, String startCursor, String endCursor, int size) {
        super(content, startCursor, endCursor, size);
    }

    public static SpecializationPageDto ofByNameAndId(List<SpecializationsDto> content,
                                                      int requestedSize,
                                                      CursorDestinationType cursorDestinationType) {
        PageDto<SpecializationsDto> page = PageDto.of(content, requestedSize, lastItem ->
                PageDto.encodeCursor(lastItem.getName() + "|" + lastItem.getId()),
                        cursorDestinationType
        );

        return new SpecializationPageDto(
                page.getContent(),
                page.getStartCursor(),
                page.getEndCursor(),
                page.getSize()
        );
    }
}