package hu.nje.todo.todo.domain.util;

import hu.nje.todo.R;

public class PriorityUiMapper {

    public static int getPriorityColorResId(Integer priority) {
        return switch (priority) {
            case 4 -> R.color.daisy_light_primary;
            case 3 -> R.color.daisy_dark_error;
            case 2 -> R.color.daisy_dark_accent;
            case 1 -> R.color.daisy_light_accent_content;
            default -> R.color.daisy_light_accent;
        };
    }

}
