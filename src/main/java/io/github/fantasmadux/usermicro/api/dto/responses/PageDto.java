package io.github.fantasmadux.usermicro.api.dto.responses;

import io.github.fantasmadux.usermicro.store.enums.CursorDestinationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Базовый DTO для пагинации")
public class PageDto<T> {

    @Schema(description = "Список элементов")
    private List<T> content;

    @Schema(description = "Курсор первого эелмента текущей страницы", nullable = true)
    private String startCursor;

    @Schema(description = "Курсор последнего элемента текущей страницы", nullable = true)
    private String endCursor;

    @Schema(description = "Есть ли предыдущая страница")
    private boolean hasPreviousPage;

    @Schema(description = "Есть ли следующая страница")
    private boolean hasNextPage;

    @Schema(description = "Запрошенный размер страницы")
    private int size;

    public static <T> PageDto<T> of(List<T> content, int requestedSize, CursorCreator<T> cursorCreator,
                                    CursorDestinationType cursorDestinationType, String cursorName,
                                    UUID cursorId) {

        boolean isFirstPage = cursorName == null && cursorId == null;
        boolean hasExtraPage = content.size() > requestedSize;
        List<T> actualContent;

        boolean hasNextPage;
        boolean hasPreviousPage;

        if (cursorDestinationType.isBefore()) {
            hasNextPage = true;
            hasPreviousPage = hasExtraPage;
            actualContent = hasExtraPage ? content.subList(content.size() - requestedSize, content.size()) : content;
            Collections.reverse(actualContent);
        } else {
            hasPreviousPage = !isFirstPage;
            hasNextPage = hasExtraPage;
            actualContent = hasExtraPage ? content.subList(0, requestedSize) : content;
        }

        String startCursor = null;
        String endCursor = null;

        if (!actualContent.isEmpty() && cursorCreator != null) {
            T firstItem = actualContent.get(0);
            startCursor = cursorCreator.createCursor(firstItem);

            T lastItem = actualContent.get(actualContent.size() - 1);
            endCursor = cursorCreator.createCursor(lastItem);
        }

        return PageDto.<T>builder()
                .content(actualContent)
                .endCursor(endCursor)
                .startCursor(startCursor)
                .size(requestedSize)
                .hasPreviousPage(hasPreviousPage)
                .hasNextPage(hasNextPage)
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
