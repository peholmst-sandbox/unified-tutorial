package com.example.application.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class DateFormatUtil {

    private DateFormatUtil() {
    }

    public static String formatInstant(Instant instant, Locale locale) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(locale)
                .format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
    }
}
