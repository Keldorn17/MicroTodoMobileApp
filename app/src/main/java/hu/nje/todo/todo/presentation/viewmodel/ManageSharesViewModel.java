package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
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
import lombok.Setter;

@HiltViewModel
public class ManageSharesViewModel extends ViewModel {

    private final GetTodoSharesUseCase getTodoSharesUseCase;
    private final ShareTodoUseCase shareTodoUseCase;
    private final DeleteTodoShareUseCase deleteTodoShareUseCase;

    private final MutableLiveData<List<TodoShareResponse>> shares = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Setter
    private Long todoId;

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

    public void loadShares() {
        if (todoId == null) return;
        loading.setValue(true);
        getTodoSharesUseCase.execute(todoId, 0, 100, new TodoRepository.TodoCallback<TodoSharesResponse>() {
            @Override
            public void onSuccess(TodoSharesResponse response) {
                loading.setValue(false);
                if (response != null && response.getContent() != null) {
                    shares.setValue(new ArrayList<>(response.getContent()));
                }
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void shareTodo(String email, int accessLevel) {
        if (todoId == null) return;
        loading.setValue(true);
        TodoShareRequest request = TodoShareRequest.builder()
                .email(email)
                .accessLevel(accessLevel)
                .build();
        shareTodoUseCase.execute(todoId, request, new TodoRepository.TodoCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                loadShares();
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void deleteShare(String email) {
        if (todoId == null) return;
        loading.setValue(true);
        deleteTodoShareUseCase.execute(todoId, email, new TodoRepository.TodoCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                loadShares();
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }
}
