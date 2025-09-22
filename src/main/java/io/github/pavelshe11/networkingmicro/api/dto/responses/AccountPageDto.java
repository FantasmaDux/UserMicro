package io.github.pavelshe11.networkingmicro.api.dto.responses;

import io.github.pavelshe11.networkingmicro.store.enums.CursorDestinationType;

import java.util.List;

public class AccountPageDto extends PageDto<AccountShortDto>{
    public AccountPageDto(List<AccountShortDto> content, String startCursor, String endCursor, int size) {
        super(content, startCursor, endCursor, size);
    }

    public static AccountPageDto ofByFullName(List<AccountShortDto> content, int requestedSize, CursorDestinationType cursorDestinationType) {
        PageDto<AccountShortDto> page = PageDto.of(content, requestedSize, lastItem ->
                PageDto.encodeCursor(lastItem.getLastName() + "|" + lastItem.getId()),
                cursorDestinationType
        );

        return new AccountPageDto(
                page.getContent(),
                page.getStartCursor(),
                page.getEndCursor(),
                page.getSize()
        );
    }
}
