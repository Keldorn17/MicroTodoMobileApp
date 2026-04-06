package hu.nje.todo.todo.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {
    NO_PRIORITY(0, "No Priority"),
    LOW(1, "Low"),
    NORMAL(2, "Normal"),
    HIGH(3, "High"),
    CRITICAL(4, "Critical");

    private final int value;
    private final String displayName;

    public static Priority fromValue(Integer value) {
        if (value == null) {
            return NO_PRIORITY;
        }
        for (Priority p : values()) {
            if (p.getValue() == value) {
                return p;
            }
        }
        return NO_PRIORITY;
    }
}
