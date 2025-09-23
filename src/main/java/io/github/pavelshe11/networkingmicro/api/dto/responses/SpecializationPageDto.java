package io.github.pavelshe11.networkingmicro.api.dto.responses;

import io.github.pavelshe11.networkingmicro.store.enums.CursorDestinationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Ответ на запрос получения специализаций")
public class SpecializationPageDto extends PageDto<SpecializationsDto> {

    public SpecializationPageDto(List<SpecializationsDto> content, String startCursor, String endCursor, boolean hasPreviousPage, boolean hasNextPage, int size) {
        super(content, startCursor, endCursor, hasPreviousPage, hasNextPage, size);
    }

    public static SpecializationPageDto ofByNameAndId(List<SpecializationsDto> content,
                                                      int requestedSize,
                                                      CursorDestinationType cursorDestinationType,
                                                      String cursorName,
                                                      UUID cursorId) {
        PageDto<SpecializationsDto> page = PageDto.of(content, requestedSize, lastItem ->
                PageDto.encodeCursor(lastItem.getName() + "|" + lastItem.getId()),
                        cursorDestinationType,
                cursorName,
                cursorId
        );

        return new SpecializationPageDto(
                page.getContent(),
                page.getStartCursor(),
                page.getEndCursor(),
                page.isHasPreviousPage(),
                page.isHasNextPage(),
                page.getSize()
        );
    }
}