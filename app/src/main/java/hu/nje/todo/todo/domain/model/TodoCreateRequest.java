package hu.nje.todo.todo.domain.model;

import java.time.ZonedDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoCreateRequest {

    private String title;
    private String description;
    private ZonedDateTime deadline;
    private Boolean completed;
    private Long parent;
    private Integer priority;
    private Set<String> categories;

}
