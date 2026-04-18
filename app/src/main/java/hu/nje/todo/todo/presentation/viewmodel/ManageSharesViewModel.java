package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    private List<TodoShareResponse> originalShares = new ArrayList<>();
    private List<TodoShareResponse> currentShares = new ArrayList<>();

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

    public LiveData<Boolean> isSaveSuccess() {
        return saveSuccess;
    }

    public void loadShares(Long todoId) {
        if (todoId == null) return;
        loading.postValue(true);
        getTodoSharesUseCase.execute(todoId, 0, 100, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(TodoSharesResponse response) {
                loading.postValue(false);
                if (response != null && response.getContent() != null) {
                    originalShares = new ArrayList<>(response.getContent());
                    currentShares = new ArrayList<>();
                    for (TodoShareResponse s : originalShares) {
                        currentShares.add(TodoShareResponse.builder()
                                .email(s.getEmail())
                                .accessLevel(s.getAccessLevel())
                                .build());
                    }
                    shares.postValue(new ArrayList<>(currentShares));
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

    public void shareTodoLocal(String email, int accessLevel) {
        boolean found = false;
        for (TodoShareResponse share : currentShares) {
            if (share.getEmail().equalsIgnoreCase(email)) {
                share.setAccessLevel(accessLevel);
                found = true;
                break;
            }
        }
        if (!found) {
            currentShares.add(TodoShareResponse.builder()
                    .email(email)
                    .accessLevel(accessLevel)
                    .build());
        }
        shares.setValue(new ArrayList<>(currentShares));
    }

    public void deleteShareLocal(String email) {
        currentShares.removeIf(share -> share.getEmail().equalsIgnoreCase(email));
        shares.setValue(new ArrayList<>(currentShares));
    }

    public void saveChanges(Long todoId) {
        if (todoId == null) return;
        loading.setValue(true);

        Map<String, TodoShareResponse> originalMap = new HashMap<>();
        for (TodoShareResponse s : originalShares) {
            originalMap.put(s.getEmail().toLowerCase(), s);
        }

        Map<String, TodoShareResponse> currentMap = new HashMap<>();
        for (TodoShareResponse s : currentShares) {
            currentMap.put(s.getEmail().toLowerCase(), s);
        }

        List<String> toDelete = new ArrayList<>();
        for (Map.Entry<String, TodoShareResponse> entry : originalMap.entrySet()) {
            if (!currentMap.containsKey(entry.getKey())) {
                toDelete.add(entry.getValue().getEmail());
            }
        }

        List<TodoShareRequest> toShare = new ArrayList<>();
        for (Map.Entry<String, TodoShareResponse> entry : currentMap.entrySet()) {
            String lowerEmail = entry.getKey();
            TodoShareResponse current = entry.getValue();
            TodoShareResponse original = originalMap.get(lowerEmail);

            if (original == null || !original.getAccessLevel().equals(current.getAccessLevel())) {
                toShare.add(TodoShareRequest.builder()
                        .email(current.getEmail())
                        .accessLevel(current.getAccessLevel())
                        .build());
            }
        }

        int totalOperations = toDelete.size() + toShare.size();
        if (totalOperations == 0) {
            loading.setValue(false);
            saveSuccess.setValue(true);
            return;
        }

        AtomicInteger completedOps = new AtomicInteger(0);
        AtomicInteger failedOps = new AtomicInteger(0);

        TodoRepository.TodoCallback<Void> callback = new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(Void response) {
                checkCompletion();
            }

            @Override
            public void onError(String message) {
                failedOps.incrementAndGet();
                if ("ACCESS_DENIED".equals(message)) {
                    accessDenied.postValue(true);
                } else {
                    errorMessage.postValue(message);
                }
                checkCompletion();
            }

            private void checkCompletion() {
                if (completedOps.incrementAndGet() == totalOperations) {
                    loading.postValue(false);
                    if (failedOps.get() == 0) {
                        saveSuccess.postValue(true);
                    }
                }
            }
        };

        for (String email : toDelete) {
            deleteTodoShareUseCase.execute(todoId, email, callback);
        }

        for (TodoShareRequest request : toShare) {
            shareTodoUseCase.execute(todoId, request, callback);
        }
    }
}
