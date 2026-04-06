package hu.nje.todo.todo.data.source;

import hu.nje.todo.todo.domain.model.QueryMode;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.model.TodoStatisticsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TodoApi {

    @GET("/api/v1/todos")
    Call<TodoResponse> getTodos(@Query("mode") QueryMode queryMode);

    @GET("/api/v1/todos/statistics")
    Call<TodoStatisticsResponse> getStatistics(
            @Query("mode") String mode,
            @Query("groupBy") String groupBy,
            @Query("pageNumber") Integer pageNumber,
            @Query("pageSize") Integer pageSize
    );

}
