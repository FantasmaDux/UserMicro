package io.github.pavelshe11.networkingmicro.api.dto.responses;

import io.github.pavelshe11.networkingmicro.store.enums.CursorDestinationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

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

    @Schema(description = "Запрошенный размер страницы")
    private int size;

    public static <T> PageDto<T> of(List<T> content, int requestedSize, CursorCreator<T> cursorCreator,
                                    CursorDestinationType cursorDestinationType) {

        boolean hasExtraPage = content.size() > requestedSize;
        List<T> actualContent;

        if (cursorDestinationType.isBefore()) {
            actualContent = hasExtraPage ? content.subList(content.size() - requestedSize, content.size()) : content;
            Collections.reverse(actualContent);
        } else {
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
