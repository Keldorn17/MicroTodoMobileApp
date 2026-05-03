package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.repository.TodoRepository;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetTodoUseCase {

    private final TodoRepository todoRepository;

    public void execute(Long todoId, TodoRepository.TodoCallback<Todo> callback) {
        todoRepository.getTodo(todoId, callback);
    }

}
