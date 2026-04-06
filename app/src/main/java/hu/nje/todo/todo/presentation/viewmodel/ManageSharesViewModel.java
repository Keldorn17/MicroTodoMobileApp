package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.model.TodoShareRequest;
import hu.nje.todo.todo.domain.model.TodoShareResponse;
import hu.nje.todo.todo.domain.model.TodoSharesResponse;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import hu.nje.todo.todo.domain.usecase.DeleteTodoShareUseCase;
import hu.nje.todo.todo.domain.usecase.GetTodoSharesUseCase;
import hu.nje.todo.todo.domain.usecase.ShareTodoUseCase;

@HiltViewModel
public class ManageSharesViewModel extends ViewModel {

    private final GetTodoSharesUseCase getTodoSharesUseCase;
    private final ShareTodoUseCase shareTodoUseCase;
    private final DeleteTodoShareUseCase deleteTodoShareUseCase;

    private final MutableLiveData<List<TodoShareResponse>> shares = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accessDenied = new MutableLiveData<>();

    @Inject
    public ManageSharesViewModel(GetTodoSharesUseCase getTodoSharesUseCase,
                                 ShareTodoUseCase shareTodoUseCase,
                                 DeleteTodoShareUseCase deleteTodoShareUseCase) {
        this.getTodoSharesUseCase = getTodoSharesUseCase;
        this.shareTodoUseCase = shareTodoUseCase;
        this.deleteTodoShareUseCase = deleteTodoShareUseCase;
    }

    public LiveData<List<TodoShareResponse>> getShares() {
        return shares;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> isAccessDenied() {
        return accessDenied;
    }

    public void loadShares(Long todoId) {
        if (todoId == null) return;
        loading.postValue(true);
        getTodoSharesUseCase.execute(todoId, 0, 100, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(TodoSharesResponse response) {
                loading.postValue(false);
                if (response != null) {
                    shares.postValue(response.getContent());
                }
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                if ("ACCESS_DENIED".equals(message)) {
                    accessDenied.postValue(true);
                } else {
                    errorMessage.postValue(message);
                }
            }
        });
    }

    public void shareTodo(Long todoId, String email, int accessLevel) {
        if (todoId == null) return;
        loading.postValue(true);
        TodoShareRequest request = TodoShareRequest.builder()
                .email(email)
                .accessLevel(accessLevel)
                .build();
        shareTodoUseCase.execute(todoId, request, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(Void response) {
                loadShares(todoId);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                if ("ACCESS_DENIED".equals(message)) {
                    accessDenied.postValue(true);
                } else {
                    errorMessage.postValue(message);
                }
            }
        });
    }

    public void deleteShare(Long todoId, String email) {
        if (todoId == null) return;
        loading.postValue(true);
        deleteTodoShareUseCase.execute(todoId, email, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(Void response) {
                loadShares(todoId);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                if ("ACCESS_DENIED".equals(message)) {
                    accessDenied.postValue(true);
                } else {
                    errorMessage.postValue(message);
                }
            }
        });
    }
}
