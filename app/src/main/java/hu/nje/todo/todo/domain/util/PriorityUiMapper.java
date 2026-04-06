package hu.nje.todo.todo.domain.util;

import hu.nje.todo.R;
import hu.nje.todo.todo.domain.model.Priority;

public class PriorityUiMapper {

    public static int getPriorityColorResId(Integer priorityValue) {
        Priority priority = Priority.fromValue(priorityValue);
        return switch (priority) {
            case CRITICAL -> R.color.daisy_light_primary;
            case HIGH -> R.color.daisy_dark_error;
            case NORMAL -> R.color.daisy_dark_accent;
            case LOW -> R.color.daisy_light_accent_content;
            default -> R.color.daisy_light_accent;
        };
    }

}
