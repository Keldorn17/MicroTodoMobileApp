package hu.nje.todo.todo.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccessLevel {
    READ(0),
    WRITE(1),
    MANAGE(2),
    OWNER(3);

    private final int value;

    public static AccessLevel fromValue(int value) {
        for (AccessLevel level : AccessLevel.values()) {
            if (level.value == value) {
                return level;
            }
        }
        return READ;
    }
}
