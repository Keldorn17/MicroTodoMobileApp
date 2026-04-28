package hu.nje.todo.todo.presentation.viewmodel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoCreateRequest;
import hu.nje.todo.todo.domain.model.TodoShareRequest;
import hu.nje.todo.todo.domain.model.TodoShareResponse;
import hu.nje.todo.todo.domain.model.TodoSharesResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import hu.nje.todo.todo.domain.usecase.CreateTodoUseCase;
import hu.nje.todo.todo.domain.usecase.DeleteTodoShareUseCase;
import hu.nje.todo.todo.domain.usecase.GetTodoSharesUseCase;
import hu.nje.todo.todo.domain.usecase.PatchTodoUseCase;
import hu.nje.todo.todo.domain.usecase.ShareTodoUseCase;

@HiltViewModel
public class TodoEditorViewModel extends ViewModel {

    private final CreateTodoUseCase createTodoUseCase;
    private final PatchTodoUseCase patchTodoUseCase;
    private final GetTodoSharesUseCase getTodoSharesUseCase;
    private final ShareTodoUseCase shareTodoUseCase;
    private final DeleteTodoShareUseCase deleteTodoShareUseCase;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    private final MutableLiveData<ZonedDateTime> deadline = new MutableLiveData<>();

    private final MutableLiveData<List<TodoShareResponse>> shares =
            new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> sharesLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accessDenied = new MutableLiveData<>();

    private final MutableLiveData<Set<String>> categories = new MutableLiveData<>(new HashSet<>());

    @Getter
    @Setter
    private Long todoId = null;

    @Setter
    private boolean canEdit = true;

    @Getter
    @Setter
    private boolean isLoaded = false;

    private List<TodoShareResponse> originalShares = new ArrayList<>();

    @Inject
    public TodoEditorViewModel(CreateTodoUseCase createTodoUseCase,
            PatchTodoUseCase patchTodoUseCase,
            GetTodoSharesUseCase getTodoSharesUseCase,
            ShareTodoUseCase shareTodoUseCase,
            DeleteTodoShareUseCase deleteTodoShareUseCase) {
        this.createTodoUseCase = createTodoUseCase;
        this.patchTodoUseCase = patchTodoUseCase;
        this.getTodoSharesUseCase = getTodoSharesUseCase;
        this.shareTodoUseCase = shareTodoUseCase;
        this.deleteTodoShareUseCase = deleteTodoShareUseCase;
    }

    public boolean canEdit() {
        return canEdit;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> isSuccess() {
        return success;
    }

    public LiveData<ZonedDateTime> getDeadline() {
        return deadline;
    }

    public LiveData<List<TodoShareResponse>> getShares() {
        return shares;
    }

    public LiveData<Boolean> isSharesLoading() {
        return sharesLoading;
    }

    public LiveData<Boolean> isAccessDenied() {
        return accessDenied;
    }

    public LiveData<Set<String>> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> newCategories) {
        categories.setValue(newCategories);
    }

    public void setDeadline(ZonedDateTime dt) {
        deadline.setValue(dt);
    }

    public void setShares(List<TodoShareResponse> updatedShares) {
        shares.setValue(new ArrayList<>(updatedShares));
    }

    public void loadShares(Long id) {
        if (id == null) {
            return;
        }
        sharesLoading.postValue(true);
        getTodoSharesUseCase.execute(id, 0, 100, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(TodoSharesResponse response) {
                sharesLoading.postValue(false);
                if (response != null && response.getContent() != null) {
                    originalShares = new ArrayList<>(response.getContent());
                    shares.postValue(new ArrayList<>(originalShares));
                }
            }

            @Override
            public void onError(String message) {
                sharesLoading.postValue(false);
                if ("ACCESS_DENIED".equals(message)) {
                    accessDenied.postValue(true);
                } else {
                    errorMessage.postValue(message);
                }
            }
        });
    }

    public void saveTodo(String title, String description, Integer priority, boolean isCompleted) {
        loading.postValue(true);
        ZonedDateTime currentDeadline = deadline.getValue();
        if (currentDeadline != null) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            if (currentDeadline.isBefore(now)) {
                currentDeadline = now.plusMinutes(1);
                deadline.postValue(currentDeadline);
            }
        }
        Set<String> currentCategories =
                categories.getValue() != null ? categories.getValue() : new HashSet<>();
        if (todoId == null) {
            TodoCreateRequest request = TodoCreateRequest.builder()
                    .title(title)
                    .description(description)
                    .priority(priority)
                    .deadline(currentDeadline)
                    .completed(isCompleted)
                    .categories(currentCategories)
                    .build();
            createTodoUseCase.execute(request, new TodoRepository.TodoCallback<>() {
                @Override
                public void onSuccess(Todo response) {
                    saveShares(response.getId());
                }

                @Override
                public void onError(String message) {
                    loading.postValue(false);
                    errorMessage.postValue(message);
                }
            });
        } else {
            TodoUpdateRequest request = TodoUpdateRequest.builder()
                    .title(title)
                    .description(description)
                    .priority(priority)
                    .deadline(currentDeadline)
                    .completed(isCompleted)
                    .categories(currentCategories)
                    .build();
            patchTodoUseCase.execute(todoId, request, new TodoRepository.TodoCallback<>() {
                @Override
                public void onSuccess(Todo response) {
                    saveShares(todoId);
                }

                @Override
                public void onError(String message) {
                    loading.postValue(false);
                    errorMessage.postValue(message);
                }
            });
        }
    }

    private void saveShares(Long savedTodoId) {
        List<TodoShareResponse> currentShares = shares.getValue();
        if (currentShares == null) {
            currentShares = new ArrayList<>();
        }
        Map<String, TodoShareResponse> originalMap = new HashMap<>();
        for (TodoShareResponse s : originalShares) {
            if (s.getEmail() != null) {
                originalMap.put(s.getEmail().toLowerCase(), s);
            }
        }
        Map<String, TodoShareResponse> currentMap = new HashMap<>();
        for (TodoShareResponse s : currentShares) {
            if (s.getEmail() != null) {
                currentMap.put(s.getEmail().toLowerCase(), s);
            }
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
            Integer originalLevel = original != null ? original.getAccessLevel() : null;
            Integer currentLevel = current.getAccessLevel();
            boolean isLevelDifferent = false;
            if (originalLevel == null && currentLevel != null) {
                isLevelDifferent = true;
            } else if (originalLevel != null && !originalLevel.equals(currentLevel)) {
                isLevelDifferent = true;
            }
            if (original == null || isLevelDifferent) {
                toShare.add(TodoShareRequest.builder()
                        .email(current.getEmail())
                        .accessLevel(current.getAccessLevel())
                        .build());
            }
        }
        int totalOperations = toDelete.size() + toShare.size();
        if (totalOperations == 0) {
            loading.postValue(false);
            success.postValue(true);
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
                        success.postValue(true);
                    }
                }
            }
        };
        for (String email : toDelete) {
            deleteTodoShareUseCase.execute(savedTodoId, email, callback);
        }
        for (TodoShareRequest request : toShare) {
            shareTodoUseCase.execute(savedTodoId, request, callback);
        }
    }

}
