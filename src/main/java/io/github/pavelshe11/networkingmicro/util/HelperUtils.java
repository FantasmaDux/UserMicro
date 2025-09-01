package io.github.pavelshe11.networkingmicro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;

public class HelperUtils {
    private static final Logger log = LoggerFactory.getLogger(HelperUtils.class);

    public static String fetchFaviconUrl(String contact) {
        String faviconUrl = "";

        try {
            String domain;

            if (contact.contains("@")) {
                domain = contact.substring(contact.indexOf("@") + 1);
            } else {
                if (!contact.startsWith("http://") && !contact.startsWith("https://")) {
                    contact = "https://" + contact;
                }

                URI uri = URI.create(contact);
                URL url = uri.toURL();
                domain = url.getHost();
            }

            if (!domain.isEmpty()) {
                faviconUrl = "https://" + domain + "/favicon.ico";
            }

        } catch (Exception e) {
            log.error("Ошибка извлечения favicon.");
        }

        return faviconUrl;
    }
}
