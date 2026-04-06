package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import hu.nje.todo.todo.domain.repository.TodoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DeleteTodoShareUseCase {

    private final TodoRepository todoRepository;

    public void execute(Long todoId, String email, TodoRepository.TodoCallback<Void> callback) {
        todoRepository.deleteTodoShare(todoId, email, callback);
    }
}
