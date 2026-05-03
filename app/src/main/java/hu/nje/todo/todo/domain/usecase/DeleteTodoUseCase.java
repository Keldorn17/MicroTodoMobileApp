package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import hu.nje.todo.todo.domain.repository.TodoRepository;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DeleteTodoUseCase {

    private final TodoRepository todoRepository;

    public void execute(Long todoId, TodoRepository.TodoCallback<Void> callback) {
        todoRepository.deleteTodo(todoId, callback);
    }

}
