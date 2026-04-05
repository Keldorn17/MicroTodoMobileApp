package hu.nje.todo.todo.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import hu.nje.todo.todo.data.source.TodoApi;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoRequest;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;
import hu.nje.todo.todo.domain.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepository {

    private static final String TAG = "TodoRepositoryImpl";

    private final TodoApi todoApi;

    @Override
    public void getTodos(TodoRequest request, TodoCallback<TodoResponse> callback) {
        todoApi.getTodos(request.getQueryMode()).enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<TodoResponse> call,
                    @NonNull Response<TodoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "onResponse: Failed to get todos " + response.code());
                    callback.onError("Failed to get Todos");
                }
            }

            @Override
            public void onFailure(@NonNull Call<TodoResponse> call, @NonNull Throwable throwable) {
                callback.onError("Network error: " + throwable.getMessage());
            }

        });
    }

    @Override
    public void patchTodo(Long todoId, TodoUpdateRequest request,
            TodoCallback<Todo> callback) {
        todoApi.patchTodos(todoId, request).enqueue(new Callback<>() {

            @Override
            public void onResponse(@NotNull Call<Todo> call,
                    @NotNull Response<Todo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG,
                            "onResponse: Failed to patch todo by id: " + todoId + ", status code:" +
                                    " " + response.code());
                    callback.onError("Failed to patch Todo by id: " + todoId);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Todo> call, @NonNull Throwable throwable) {
                callback.onError("Network error: " + throwable.getMessage());
            }
        });
    }

}
