package hu.nje.todo.todo.domain.repository;

import hu.nje.todo.todo.domain.model.TodoRequest;
import hu.nje.todo.todo.domain.model.TodoResponse;

public interface TodoRepository {

    void getTodos(TodoRequest request, TodoCallback<TodoResponse> callback);

    interface TodoCallback<T> {

        void onSuccess(T response);

        void onError(String errorMessage);

    }

}
