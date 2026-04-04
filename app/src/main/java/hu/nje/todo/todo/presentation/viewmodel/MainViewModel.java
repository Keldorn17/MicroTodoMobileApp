package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.auth.domain.usecase.GetAuthStateUseCase;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final MutableLiveData<Boolean> authorized = new MutableLiveData<>();

    @Inject
    public MainViewModel(GetAuthStateUseCase getAuthStateUseCase) {
        this.authorized.postValue(getAuthStateUseCase.isAuthorized());
    }

    public LiveData<Boolean> isAuthorized() {
        return authorized;
    }

}
