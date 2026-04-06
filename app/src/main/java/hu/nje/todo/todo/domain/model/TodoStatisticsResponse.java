package hu.nje.todo.todo.domain.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatisticsResponse {
    private Long total;
    private Long finished;
    private Long unfinished;
    private Map<String, TodoStatisticsEntryResponse> statistics;
}
