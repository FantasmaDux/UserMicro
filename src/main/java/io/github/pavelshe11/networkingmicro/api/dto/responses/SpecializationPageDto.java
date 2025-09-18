package io.github.pavelshe11.networkingmicro.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Ответ на запрос получения специализаций")
public class SpecializationPageDto extends PageDto<SpecializationsDto> {

    public SpecializationPageDto(List<SpecializationsDto> content, String nextCursor, boolean hasNext, int size) {
        super(content, nextCursor, hasNext, size);
    }

    public static SpecializationPageDto ofByNameAndId(List<SpecializationsDto> content, int requestedSize) {
        PageDto<SpecializationsDto> page = PageDto.of(content, requestedSize, lastItem ->
                PageDto.encodeCursor(lastItem.getName() + "|" + lastItem.getId())
        );

        return new SpecializationPageDto(
                page.getContent(), page.getNextCursor(), page.isHasNext(), page.getSize()
        );
    }
}