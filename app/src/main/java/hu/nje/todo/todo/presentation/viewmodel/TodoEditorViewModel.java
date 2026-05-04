package hu.nje.todo.todo.presentation.viewmodel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoCreateRequest;
import hu.nje.todo.todo.domain.model.TodoShareResponse;
import hu.nje.todo.todo.domain.model.TodoSharesResponse;
import hu.nje.todo.todo.domain.model.TodoUpdateRequest;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import hu.nje.todo.todo.domain.usecase.CreateTodoUseCase;
import hu.nje.todo.todo.domain.usecase.DeleteTodoUseCase;
import hu.nje.todo.todo.domain.usecase.GetTodoSharesUseCase;
import hu.nje.todo.todo.domain.usecase.GetTodoUseCase;
import hu.nje.todo.todo.domain.usecase.PatchTodoUseCase;

@HiltViewModel
public class TodoEditorViewModel extends ViewModel {

    private final CreateTodoUseCase createTodoUseCase;
    private final PatchTodoUseCase patchTodoUseCase;
    private final GetTodoSharesUseCase getTodoSharesUseCase;
    private final GetTodoUseCase getTodoUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleted = new MutableLiveData<>();

    private final MutableLiveData<ZonedDateTime> deadline = new MutableLiveData<>();

    private final MutableLiveData<List<TodoShareResponse>> shares =
            new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> sharesLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accessDenied = new MutableLiveData<>();

    private final MutableLiveData<Set<String>> categories = new MutableLiveData<>(new HashSet<>());
    private final MutableLiveData<Todo> loadedTodo = new MutableLiveData<>();

    @Getter
    @Setter
    private Long todoId = null;

    @Setter
    private boolean canEdit = true;

    @Setter
    private boolean canDelete = true;

    @Getter
    @Setter
    private boolean isLoaded = false;

    @Inject
    public TodoEditorViewModel(CreateTodoUseCase createTodoUseCase,
            PatchTodoUseCase patchTodoUseCase,
            GetTodoSharesUseCase getTodoSharesUseCase,
            GetTodoUseCase getTodoUseCase,
            DeleteTodoUseCase deleteTodoUseCase) {
        this.createTodoUseCase = createTodoUseCase;
        this.patchTodoUseCase = patchTodoUseCase;
        this.getTodoSharesUseCase = getTodoSharesUseCase;
        this.getTodoUseCase = getTodoUseCase;
        this.deleteTodoUseCase = deleteTodoUseCase;
    }

    public boolean canEdit() {
        return canEdit;
    }

    public boolean canDelete() {
        return canDelete;
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

    public LiveData<Boolean> isDeleted() {
        return deleted;
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

    public LiveData<Todo> getLoadedTodo() {
        return loadedTodo;
    }

    public void loadTodoData(Long id) {
        if (id == null) {
            return;
        }
        loading.postValue(true);
        getTodoUseCase.execute(id, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(Todo response) {
                loading.postValue(false);
                if (response != null) {
                    loadedTodo.postValue(response);
                }
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void clearLoadedTodo() {
        loadedTodo.setValue(null);
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
                    shares.postValue(new ArrayList<>(response.getContent()));
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

    public void deleteTodo() {
        if (todoId == null) {
            return;
        }
        loading.postValue(true);
        deleteTodoUseCase.execute(todoId, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(Void response) {
                loading.postValue(false);
                deleted.postValue(true);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

}
