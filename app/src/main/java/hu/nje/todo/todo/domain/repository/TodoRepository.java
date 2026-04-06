package hu.nje.todo.todo.domain.repository;

import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoCreateRequest;
import hu.nje.todo.todo.domain.model.TodoRequest;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.model.TodoShareRequest;
import hu.nje.todo.todo.domain.model.TodoSharesResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;

public interface TodoRepository {

    void getTodos(TodoRequest request, TodoCallback<TodoResponse> callback);

    void createTodo(TodoCreateRequest request, TodoCallback<Todo> callback);

    void patchTodo(Long todoId, TodoUpdateRequest request, TodoCallback<Todo> callback);

    void getTodoShares(Long todoId, int page, int size, TodoCallback<TodoSharesResponse> callback);

    void shareTodo(Long todoId, TodoShareRequest request, TodoCallback<Void> callback);

    void deleteTodoShare(Long todoId, String email, TodoCallback<Void> callback);

    interface TodoCallback<T> {

        void onSuccess(T response);

        void onError(String errorMessage);

    }

}
