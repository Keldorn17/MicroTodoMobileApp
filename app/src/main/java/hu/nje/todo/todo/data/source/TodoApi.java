package hu.nje.todo.todo.data.source;

import hu.nje.todo.todo.domain.model.QueryMode;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TodoApi {

    @GET("/api/v1/todos")
    Call<TodoResponse> getTodos(@Query("mode") QueryMode queryMode);

    @PATCH("/api/v1/todos/{id}")
    Call<Todo> patchTodos(@Path("id") Long id, @Body TodoUpdateRequest request);

}
