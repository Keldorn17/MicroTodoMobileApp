package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;
import hu.nje.todo.todo.domain.repository.TodoRepository;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PatchTodoUseCase {

    private final TodoRepository todoRepository;

    public void execute(Long todoId, TodoUpdateRequest request,
            TodoRepository.TodoCallback<Todo> callback) {
        todoRepository.patchTodo(todoId, request, callback);
    }

}
