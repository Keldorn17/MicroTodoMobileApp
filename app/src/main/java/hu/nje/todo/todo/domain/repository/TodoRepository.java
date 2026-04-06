package hu.nje.todo.todo.domain.repository;

import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoRequest;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;

public interface TodoRepository {

    void getTodos(TodoRequest request, TodoCallback<TodoResponse> callback);

    void patchTodo(Long todoId, TodoUpdateRequest request, TodoCallback<Todo> callback);

    interface TodoCallback<T> {

        void onSuccess(T response);

        void onError(String errorMessage);

    }

}
