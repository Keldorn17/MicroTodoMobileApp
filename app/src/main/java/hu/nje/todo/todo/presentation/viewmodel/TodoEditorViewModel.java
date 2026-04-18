package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.inject.Inject;

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
    
    private final MutableLiveData<List<TodoShareResponse>> shares = new MutableLiveData<>();
    private final MutableLiveData<Boolean> sharesLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accessDenied = new MutableLiveData<>();
    
    private final MutableLiveData<java.util.Set<String>> categories = new MutableLiveData<>(new java.util.HashSet<>());
    private Long todoId = null;
    private boolean canEdit = true;
    private boolean isLoaded = false;
    private ZonedDateTime originalDeadline = null;

    public void setOriginalDeadline(ZonedDateTime dt) {
        this.originalDeadline = dt;
    }

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

    public Long getTodoId() {
        return todoId;
    }

    public void setTodoId(Long todoId) {
        this.todoId = todoId;
    }

    public boolean canEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
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

    public LiveData<java.util.Set<String>> getCategories() {
        return categories;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public void setCategories(java.util.Set<String> newCategories) {
        categories.setValue(newCategories);
    }

    public void addCategory(String category) {
        java.util.Set<String> current = categories.getValue();
        if (current != null) {
            current.add(category);
            categories.setValue(current);
        }
    }

    public void removeCategory(String category) {
        java.util.Set<String> current = categories.getValue();
        if (current != null) {
            current.remove(category);
            categories.setValue(current);
        }
    }

    public void setDeadline(ZonedDateTime dt) {
        deadline.setValue(dt);
    }

    public void loadShares(Long id) {
        if (id == null) return;
        sharesLoading.postValue(true);
        getTodoSharesUseCase.execute(id, 0, 100, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(TodoSharesResponse response) {
                sharesLoading.postValue(false);
                if (response != null) {
                    shares.postValue(response.getContent());
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

    public void shareTodo(String email) {
        if (todoId == null) {
            errorMessage.postValue("Cannot share an unsaved todo.");
            return;
        }
        sharesLoading.postValue(true);
        TodoShareRequest request = TodoShareRequest.builder()
                .email(email)
                .accessLevel(1)
                .build();
        shareTodoUseCase.execute(todoId, request, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(Void response) {
                loadShares(todoId);
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

    public void deleteShare(String email) {
        if (todoId == null) return;
        sharesLoading.postValue(true);
        deleteTodoShareUseCase.execute(todoId, email, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(Void response) {
                loadShares(todoId);
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
            boolean isModified = originalDeadline == null || !currentDeadline.isEqual(originalDeadline);
            if (isModified) {
                ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
                if (currentDeadline.isBefore(now)) {
                    currentDeadline = now.plusMinutes(1);
                    deadline.postValue(currentDeadline);
                }
            }
        }

        java.util.Set<String> currentCategories = categories.getValue() != null ? categories.getValue() : new java.util.HashSet<>();

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
                    loading.postValue(false);
                    success.postValue(true);
                }

                @Override
                public void onError(String message) {
                    loading.postValue(false);
                    errorMessage.postValue(message);
                }
            });
        } else {
            ZonedDateTime deadlineToSend = currentDeadline;
            if (currentDeadline != null && originalDeadline != null) {
                if (currentDeadline.isEqual(originalDeadline)) {
                    deadlineToSend = null;
                }
            } else if (currentDeadline == null && originalDeadline == null) {
                deadlineToSend = null;
            }

            TodoUpdateRequest request = TodoUpdateRequest.builder()
                    .title(title)
                    .description(description)
                    .priority(priority)
                    .deadline(deadlineToSend)
                    .completed(isCompleted)
                    .categories(currentCategories)
                    .build();

            patchTodoUseCase.execute(todoId, request, new TodoRepository.TodoCallback<>() {
                @Override
                public void onSuccess(Todo response) {
                    loading.postValue(false);
                    success.postValue(true);
                }

                @Override
                public void onError(String message) {
                    loading.postValue(false);
                    errorMessage.postValue(message);
                }
            });
        }
    }
}
