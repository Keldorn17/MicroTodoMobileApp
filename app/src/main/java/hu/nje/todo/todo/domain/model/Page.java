package hu.nje.todo.todo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    private Integer size;
    private Integer number;
    private Integer totalElements;
    private Integer totalPages;

}
