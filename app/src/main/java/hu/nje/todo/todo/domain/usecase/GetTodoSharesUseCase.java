package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import hu.nje.todo.todo.domain.model.TodoSharesResponse;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetTodoSharesUseCase {

    private final TodoRepository todoRepository;

    public void execute(Long todoId, int page, int size, TodoRepository.TodoCallback<TodoSharesResponse> callback) {
        todoRepository.getTodoShares(todoId, page, size, callback);
    }
}
