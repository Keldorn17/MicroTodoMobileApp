package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import hu.nje.todo.todo.domain.model.TodoShareRequest;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ShareTodoUseCase {

    private final TodoRepository todoRepository;

    public void execute(Long todoId, TodoShareRequest request, TodoRepository.TodoCallback<Void> callback) {
        todoRepository.shareTodo(todoId, request, callback);
    }
}
