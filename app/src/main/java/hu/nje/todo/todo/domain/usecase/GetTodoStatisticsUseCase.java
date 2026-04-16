package hu.nje.todo.todo.domain.usecase;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import hu.nje.todo.todo.domain.model.SearchRequest;
import hu.nje.todo.todo.domain.model.TodoStatisticsResponse;
import hu.nje.todo.todo.domain.repository.TodoRepository;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetTodoStatisticsUseCase {

    private final TodoRepository todoRepository;

    public void execute(SearchRequest request, String groupBy,
            TodoRepository.TodoCallback<TodoStatisticsResponse> callback) {
        todoRepository.getStatistics(request, groupBy, callback);
    }

}
