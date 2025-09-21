package io.github.pavelshe11.networkingmicro.api.dto.responses;

import java.util.List;

public class AccountPageDto extends PageDto<AccountShortDto>{
    public AccountPageDto(List<AccountShortDto> content, String nextCursor, boolean hasNext, int size) {
        super(content, nextCursor, hasNext, size);
    }

    public static AccountPageDto ofByFullName(List<AccountShortDto> content, int requestedSize) {
        PageDto<AccountShortDto> page = PageDto.of(content, requestedSize, lastItem ->
                PageDto.encodeCursor(lastItem.getLastName() + "|" + lastItem.getId())
        );

        return new AccountPageDto(
                page.getContent(), page.getNextCursor(), page.isHasNext(), page.getSize()
        );
    }
}
