package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.model.QueryMode;
import hu.nje.todo.todo.domain.model.TodoRequest;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import hu.nje.todo.todo.domain.usecase.GetTodosUseCase;

@HiltViewModel
public class TodoListViewModel extends ViewModel {

    private final GetTodosUseCase getTodosUseCase;

    private final MutableLiveData<TodoResponse> todos = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<QueryMode> queryMode = new MutableLiveData<>();


    @Inject
    public TodoListViewModel(GetTodosUseCase getTodosUseCase) {
        this.getTodosUseCase = getTodosUseCase;
    }

    public LiveData<TodoResponse> getTodos() {
        return todos;
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
        TodoRequest request = TodoRequest.builder()
                .queryMode(queryMode.getValue())
                .build();
        getTodosUseCase.execute(request, new TodoRepository.TodoCallback<>() {

            @Override
            public void onSuccess(TodoResponse response) {
                loading.postValue(false);
                todos.postValue(response);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                errorMessage.postValue(message);
            }

        });
    }

}
