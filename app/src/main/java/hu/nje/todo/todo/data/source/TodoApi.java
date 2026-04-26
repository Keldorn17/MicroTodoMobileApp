package hu.nje.todo.todo.data.source;

import java.util.Map;

import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoCreateRequest;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.model.TodoShareRequest;
import hu.nje.todo.todo.domain.model.TodoSharesResponse;
import hu.nje.todo.todo.domain.model.TodoStatisticsResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface TodoApi {

    @GET("/api/v1/todos")
    Call<TodoResponse> getTodos(@QueryMap Map<String, String> searchParams);

    @POST("/api/v1/todos")
    Call<Todo> createTodo(@Body TodoCreateRequest request);

    @PATCH("/api/v1/todos/{id}")
    Call<Todo> patchTodos(@Path("id") Long id, @Body TodoUpdateRequest request);

    @GET("/api/v1/todos/{todoId}/share")
    Call<TodoSharesResponse> getTodoShares(@Path("todoId") Long todoId, @Query("page") int page,
            @Query("size") int size);

    @PUT("/api/v1/todos/{todoId}/share")
    Call<Void> shareTodo(@Path("todoId") Long todoId, @Body TodoShareRequest request);

    @DELETE("/api/v1/todos/{todoId}/share")
    Call<Void> deleteTodoShare(@Path("todoId") Long todoId, @Query("email") String email);

    @GET("/api/v1/todos/statistics")
    Call<TodoStatisticsResponse> getStatistics(@QueryMap Map<String, String> searchParams,
            @Query("groupBy") String groupBy);

}
