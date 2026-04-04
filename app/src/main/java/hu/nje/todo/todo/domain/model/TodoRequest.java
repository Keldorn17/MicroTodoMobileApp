package hu.nje.todo.todo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequest {

    @Builder.Default
    private String search = "";

    @Builder.Default
    private String sort = "id,asc;deadline,desc;createdAt,desc";

    @Builder.Default
    private Integer pageNumber = 0;

    @Builder.Default
    private Integer pageSize = 20;

    @Builder.Default
    private QueryMode queryMode = QueryMode.OWN;

}
