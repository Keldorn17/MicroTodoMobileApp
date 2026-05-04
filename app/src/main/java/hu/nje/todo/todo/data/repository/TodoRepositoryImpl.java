package hu.nje.todo.todo.data.repository;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import java.util.function.Function;
import android.util.Log;
import androidx.annotation.NonNull;
import hu.nje.todo.todo.data.source.TodoApi;
import hu.nje.todo.todo.domain.model.SearchRequest;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoCreateRequest;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.model.TodoShareRequest;
import hu.nje.todo.todo.domain.model.TodoSharesResponse;
import hu.nje.todo.todo.domain.model.TodoStatisticsEntryResponse;
import hu.nje.todo.todo.domain.model.TodoStatisticsResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import hu.nje.todo.todo.domain.util.SearchRequestMapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepository {

    private static final String TAG = "TodoRepositoryImpl";

    private final TodoApi todoApi;

    @Override
    public void getTodos(SearchRequest request, TodoCallback<TodoResponse> callback) {
        Map<String, String> params = SearchRequestMapper.toMap(request);
        todoApi.getTodos(params).enqueue(
                buildCallback(callback, "Failed to get Todos"));
    }

    @Override
    public void createTodo(TodoCreateRequest request, TodoCallback<Todo> callback) {
        todoApi.createTodo(request).enqueue(buildCallback(callback, "Failed to create Todo"));
    }

    @Override
    public void patchTodo(Long todoId, TodoUpdateRequest request,
            TodoCallback<Todo> callback) {
        todoApi.patchTodos(todoId, request).enqueue(
                buildCallback(callback, "Failed to patch Todo by id: " + todoId));
    }

    @Override
    public void getTodoShares(Long todoId, int page, int size,
            TodoCallback<TodoSharesResponse> callback) {
        todoApi.getTodoShares(todoId, page, size).enqueue(
                buildCallback(callback, "Failed to get todo shares"));
    }

    @Override
    public void shareTodo(Long todoId, TodoShareRequest request, TodoCallback<Void> callback) {
        todoApi.shareTodo(todoId, request).enqueue(buildCallback(callback, "Failed to share todo"));
    }

    @Override
    public void deleteTodoShare(Long todoId, String email, TodoCallback<Void> callback) {
        todoApi.deleteTodoShare(todoId, email).enqueue(
                buildCallback(callback, "Failed to delete todo share"));
    }

    @Override
    public void getStatistics(SearchRequest request, String groupBy,
                              TodoCallback<TodoStatisticsResponse> callback) {
        Map<String, String> params = SearchRequestMapper.toMap(request);
        if (groupBy == null) {
            todoApi.getStatistics(params).enqueue(
                    buildCallback(callback, "Failed to get todo statistics"));
        } else {
            todoApi.getPriorityStatistics(params, groupBy).enqueue(
                    buildMappedCallback(callback, "Failed to get todo priority statistics",
                            body -> TodoStatisticsResponse.builder().statistics(body).build()));
        }
    }

    private <T> Callback<T> buildCallback(TodoCallback<T> callback, String errorMessage) {
        return buildMappedCallback(callback, errorMessage, Function.identity());
    }

    private <T, R> Callback<T> buildMappedCallback(TodoCallback<R> callback, String errorMessage,
                                                   Function<T, R> mapper) {
        return new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(mapper.apply(response.body()));
                } else {
                    Log.e(TAG, "onResponse: " + errorMessage + " status code: " + response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
                Log.e(TAG, "onFailure: Network error", throwable);
                callback.onError("Network error: " + throwable.getMessage());
            }
        };
    }

}
