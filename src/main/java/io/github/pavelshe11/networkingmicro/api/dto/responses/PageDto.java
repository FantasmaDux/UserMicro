package io.github.pavelshe11.networkingmicro.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Базовый DTO для пагинации")
public class PageDto<T> {

    @Schema(description = "Список элементов")
    private List<T> content;

    @Schema(description = "Курсор для следующей страницы", nullable = true)
    private String nextCursor;

    @Schema(description = "Есть ли следующая страница")
    private boolean hasNext;

    @Schema(description = "Запрошенный размер страницы")
    private int size;

    public static <T> PageDto<T> of(List<T> content, int requestedSize, CursorCreator<T> cursorCreator) {
        boolean hasNextPage = content.size() > requestedSize;
        List<T> actualContent = hasNextPage ?
                content.subList(0, requestedSize) : content;

        String nextCursor = null;
        if (hasNextPage && !actualContent.isEmpty() && cursorCreator != null) {
            T lastItem = actualContent.get(actualContent.size() - 1);
            nextCursor = cursorCreator.createCursor(lastItem);
        }

        return PageDto.<T>builder()
                .content(actualContent)
                .nextCursor(nextCursor)
                .hasNext(hasNextPage)
                .size(requestedSize)
                .build();
    }

    @FunctionalInterface
    public interface CursorCreator<T> {
        String createCursor(T lastItem);
    }

    public static String encodeCursor(String cursorData) {
        return Base64.getEncoder().encodeToString(cursorData.getBytes());
    }
}
