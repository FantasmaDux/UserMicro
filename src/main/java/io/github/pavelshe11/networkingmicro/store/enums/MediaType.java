package io.github.pavelshe11.networkingmicro.store.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MediaType {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_WEBP("image/webp"),
    IMAGE_SVG("image/svg+xml");

    private final String mimetype;

    public static MediaType fromMimeType(String mimetype) {
        for (MediaType type : values()) {
            if (type.getMimetype().equalsIgnoreCase(mimetype)) {
                return type;
            }
        }
        return null;
    }
}
