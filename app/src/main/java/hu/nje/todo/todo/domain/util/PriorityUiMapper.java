package hu.nje.todo.todo.domain.util;

import hu.nje.todo.R;
import hu.nje.todo.todo.domain.model.Priority;

public class PriorityUiMapper {

    public static int getPriorityColorResId(Integer priorityValue) {
        Priority priority = Priority.fromValue(priorityValue);
        return switch (priority) {
            case CRITICAL -> R.color.priority_critical;
            case HIGH -> R.color.priority_high;
            case NORMAL -> R.color.priority_normal;
            case LOW -> R.color.priority_low;
            default -> R.color.priority_none;
        };
    }

}
