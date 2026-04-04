package hu.nje.todo.auth.presentation;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.auth.domain.repository.AuthRepository;
import hu.nje.todo.auth.domain.usecase.ExchangeCodeForTokenUseCase;
import hu.nje.todo.auth.domain.usecase.GetLoginIntentUseCase;
import hu.nje.todo.auth.domain.usecase.GetRegisterIntentUseCase;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final GetLoginIntentUseCase loginIntentUseCase;
    private final GetRegisterIntentUseCase registerIntentUseCase;
    private final ExchangeCodeForTokenUseCase exchangeCodeForTokenUseCase;

    private final MutableLiveData<Boolean> authSuccess = new MutableLiveData<>();

    @Inject
    public AuthViewModel(GetLoginIntentUseCase loginIntentUseCase, GetRegisterIntentUseCase registerIntentUseCase, ExchangeCodeForTokenUseCase exchangeCodeForTokenUseCase) {
        this.loginIntentUseCase = loginIntentUseCase;
        this.registerIntentUseCase = registerIntentUseCase;
        this.exchangeCodeForTokenUseCase = exchangeCodeForTokenUseCase;
    }

    public LiveData<Boolean> getAuthSuccess() {
        return authSuccess;
    }

    public Intent login() {
        return loginIntentUseCase.getIntent();
    }

    public Intent register() {
        return registerIntentUseCase.getIntent();
    }

    public void handleAuthResult(Intent data) {
        if (data != null) {
            exchangeCodeForTokenUseCase.execute(data, authSuccess::postValue);
        } else {
            authSuccess.postValue(false);
        }
    }

}
