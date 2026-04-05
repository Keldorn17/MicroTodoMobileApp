package hu.nje.todo.todo.domain.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import hu.nje.todo.R;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    private Long id;
    private String title;
    private String description;
    private ZonedDateTime deadline;
    private Boolean completed;
    private Long parentId;
    private Boolean shared;
    private Integer priority;
    private List<String> categories;
    private Integer accessLevel;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public int getPriorityColorResId() {
        return switch (priority) {
            case 4 -> R.color.daisy_light_primary;
            case 3 -> R.color.daisy_dark_error;
            case 2 -> R.color.daisy_dark_accent;
            case 1 -> R.color.daisy_light_accent_content;
            default -> R.color.daisy_light_accent;
        };
    }

    public String getFormattedDate() {
        if (deadline == null) {
            return "";
        }
        return deadline.format(DateTimeFormatter.ofPattern("yyyy.MM.dd."));
    }

    public String getFormattedTime() {
        if (deadline == null) {
            return "";
        }
        return deadline.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}