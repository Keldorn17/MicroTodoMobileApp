package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import hu.nje.todo.todo.domain.model.TodoRequest;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetTodosUseCase {

    private final TodoRepository todoRepository;

    public void execute(TodoRequest request, TodoRepository.TodoCallback<TodoResponse> callback) {
        todoRepository.getTodos(request, callback);
    }

}
