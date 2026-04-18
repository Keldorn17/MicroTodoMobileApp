package hu.nje.todo.todo.presentation.viewmodel;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.model.QueryMode;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;
import hu.nje.todo.todo.domain.model.SearchRequest;
import hu.nje.todo.todo.domain.model.TodoStatisticsResponse;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import hu.nje.todo.todo.domain.usecase.GetTodoStatisticsUseCase;
import hu.nje.todo.todo.domain.usecase.GetTodosUseCase;
import hu.nje.todo.todo.domain.usecase.PatchTodoUseCase;

@HiltViewModel
public class TodoListViewModel extends ViewModel {

    private final GetTodosUseCase getTodosUseCase;
    private final PatchTodoUseCase patchTodoUseCase;
    private final GetTodoStatisticsUseCase getTodoStatisticsUseCase;

    private final MutableLiveData<TodoResponse> todos = new MutableLiveData<>();
    private final MutableLiveData<TodoStatisticsResponse> statistics = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<QueryMode> queryMode = new MutableLiveData<>();

    @Inject
    public TodoListViewModel(GetTodosUseCase getTodosUseCase, PatchTodoUseCase patchTodoUseCase,
            GetTodoStatisticsUseCase getTodoStatisticsUseCase) {
        this.getTodosUseCase = getTodosUseCase;
        this.patchTodoUseCase = patchTodoUseCase;
        this.getTodoStatisticsUseCase = getTodoStatisticsUseCase;
    }

    public LiveData<TodoResponse> getTodos() {
        return todos;
    }

    public LiveData<TodoStatisticsResponse> getStatistics() {
        return statistics;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<QueryMode> getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(QueryMode mode) {
        queryMode.setValue(mode);
    }

    public void fetchTodos() {
        loading.postValue(true);
        SearchRequest request = SearchRequest.builder()
                .queryMode(queryMode.getValue())
                .build();
        getTodosUseCase.execute(request, new TodoRepository.TodoCallback<>() {

            @Override
            public void onSuccess(TodoResponse response) {
                loading.postValue(false);
                todos.postValue(response);
                fetchStatistics();
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                errorMessage.postValue(message);
            }

        });
    }

    public void fetchStatistics() {
        SearchRequest request = SearchRequest.builder()
                .queryMode(queryMode.getValue())
                .build();
        getTodoStatisticsUseCase.execute(request, null,
                new TodoRepository.TodoCallback<>() {
                    @Override
                    public void onSuccess(TodoStatisticsResponse response) {
                        statistics.postValue(response);
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.postValue("Failed to fetch statistics: " + message);
                    }
                });
    }

    public void updateTodoStatus(Long todoId, boolean isCompleted) {
        TodoUpdateRequest request = TodoUpdateRequest.builder()
                .completed(isCompleted)
                .build();
        patchTodoUseCase.execute(todoId, request, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(Todo response) {
                TodoResponse currentResponse = todos.getValue();
                if (currentResponse != null && currentResponse.getContent() != null) {
                    List<Todo> currentList = new java.util.ArrayList<>(currentResponse.getContent());
                    for (int i = 0; i < currentList.size(); i++) {
                        if (currentList.get(i).getId().equals(todoId)) {
                            currentList.set(i, response);
                            break;
                        }
                    }
                    TodoResponse updatedResponse = TodoResponse.builder()
                            .content(currentList)
                            .page(currentResponse.getPage())
                            .build();
                    todos.postValue(updatedResponse);
                    fetchStatistics();
                }

            }

            @Override
            public void onError(String message) {
                errorMessage.postValue("Failed to update task: " + message);
                fetchTodos();
            }
        });
    }

}
