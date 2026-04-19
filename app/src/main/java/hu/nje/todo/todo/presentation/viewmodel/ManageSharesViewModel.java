package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.model.TodoShareResponse;

@HiltViewModel
public class ManageSharesViewModel extends ViewModel {

    private final MutableLiveData<List<TodoShareResponse>> shares = new MutableLiveData<>(new ArrayList<>());
    private List<TodoShareResponse> currentShares = new ArrayList<>();

    @Inject
    public ManageSharesViewModel() {
    }

    public LiveData<List<TodoShareResponse>> getShares() {
        return shares;
    }

    public void setInitialShares(List<TodoShareResponse> initialShares) {
        this.currentShares = new ArrayList<>(initialShares);
        this.shares.setValue(new ArrayList<>(currentShares));
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
}
