package com.yiuzg.flo.nem12.ingest.utilities;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtil
{
    public static LocalDateTime stringToLocalDateTime(String str, String format) {
        return LocalDateTime.from(DateTimeFormatter.ofPattern(format).parse(str));
    }

    public static LocalDate stringToLocalDate(String str, String format) {
        return LocalDate.from(DateTimeFormatter.ofPattern(format).parse(str));
    }
}
