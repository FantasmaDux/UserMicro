package io.github.pavelshe11.networkingmicro.api.dto.responses;

import io.github.pavelshe11.networkingmicro.store.enums.CursorDestinationType;

import java.util.List;
import java.util.UUID;

public class AccountPageDto extends PageDto<AccountShortDto>{
    public AccountPageDto(List<AccountShortDto> content, String startCursor, String endCursor, boolean hasPreviousPage, boolean hasNextPage, int size) {
        super(content, startCursor, endCursor, hasPreviousPage, hasNextPage, size);
    }

    public static AccountPageDto ofByFullName(List<AccountShortDto> content, int requestedSize, CursorDestinationType cursorDestinationType, String cursorName,
                                              UUID cursorId) {
        PageDto<AccountShortDto> page = PageDto.of(content, requestedSize, lastItem ->
                PageDto.encodeCursor(lastItem.getLastName() + "|" + lastItem.getId()),
                cursorDestinationType,
                cursorName,
                cursorId
        );

        return new AccountPageDto(
                page.getContent(),
                page.getStartCursor(),
                page.getEndCursor(),
                page.isHasPreviousPage(),
                page.isHasNextPage(),
                page.getSize()
        );
    }
}
