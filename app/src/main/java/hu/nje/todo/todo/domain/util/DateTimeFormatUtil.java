package hu.nje.todo.todo.domain.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatUtil {

    public static String getFormattedDate(ZonedDateTime deadline) {
        if (deadline == null) {
            return "";
        }
        return deadline.withZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd."));
    }

    public static String getFormattedTime(ZonedDateTime deadline) {
        if (deadline == null) {
            return "";
        }
        return deadline.withZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}
