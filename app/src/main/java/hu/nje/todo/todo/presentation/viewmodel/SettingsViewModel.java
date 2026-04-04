package hu.nje.todo.todo.presentation.viewmodel;

import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.auth.domain.usecase.GetLogoutIntentUseCase;
import hu.nje.todo.auth.domain.usecase.GetProfileUrlUseCase;
import hu.nje.todo.auth.domain.usecase.LocalLogoutUseCase;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private final GetProfileUrlUseCase getProfileUrlUseCase;
    private final GetLogoutIntentUseCase getLogoutIntentUseCase;
    private final LocalLogoutUseCase localLogoutUseCase;

    @Inject
    public SettingsViewModel(GetProfileUrlUseCase getProfileUrlUseCase, GetLogoutIntentUseCase getLogoutIntentUseCase, LocalLogoutUseCase localLogoutUseCase) {
        this.getProfileUrlUseCase = getProfileUrlUseCase;
        this.getLogoutIntentUseCase = getLogoutIntentUseCase;
        this.localLogoutUseCase = localLogoutUseCase;
    }

    public Uri getProfileEndpoint() {
        return getProfileUrlUseCase.getProfileEndpoint();
    }

    public void performLocalLogout() {
        localLogoutUseCase.clearAuthState();
    }

    public Intent getLogoutIntent() {
        return getLogoutIntentUseCase.getLogoutIntent();
    }

}
