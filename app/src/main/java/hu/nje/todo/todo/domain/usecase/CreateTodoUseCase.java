package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoCreateRequest;
import hu.nje.todo.todo.domain.repository.TodoRepository;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CreateTodoUseCase {

    private final TodoRepository todoRepository;

    public void execute(TodoCreateRequest request, TodoRepository.TodoCallback<Todo> callback) {
        todoRepository.createTodo(request, callback);
    }

}
