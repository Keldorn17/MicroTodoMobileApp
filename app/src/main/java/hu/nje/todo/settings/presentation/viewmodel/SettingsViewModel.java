package hu.nje.todo.settings.presentation.viewmodel;

import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.auth.domain.usecase.GetLogoutIntentUseCase;
import hu.nje.todo.auth.domain.usecase.GetProfileUrlUseCase;
import hu.nje.todo.auth.domain.usecase.LocalLogoutUseCase;
import hu.nje.todo.settings.domain.model.Theme;
import hu.nje.todo.settings.domain.usecase.GetThemeUseCase;
import hu.nje.todo.settings.domain.usecase.SetThemeUseCase;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private final GetProfileUrlUseCase getProfileUrlUseCase;
    private final GetLogoutIntentUseCase getLogoutIntentUseCase;
    private final LocalLogoutUseCase localLogoutUseCase;
    private final GetThemeUseCase getThemeUseCase;
    private final SetThemeUseCase setThemeUseCase;

    @Inject
    public SettingsViewModel(GetProfileUrlUseCase getProfileUrlUseCase, GetLogoutIntentUseCase getLogoutIntentUseCase, LocalLogoutUseCase localLogoutUseCase, GetThemeUseCase getThemeUseCase, SetThemeUseCase setThemeUseCase) {
        this.getProfileUrlUseCase = getProfileUrlUseCase;
        this.getLogoutIntentUseCase = getLogoutIntentUseCase;
        this.localLogoutUseCase = localLogoutUseCase;
        this.getThemeUseCase = getThemeUseCase;
        this.setThemeUseCase = setThemeUseCase;
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

    public Theme getTheme() {
        return getThemeUseCase.getTheme();
    }

    public void setTheme(Theme theme) {
        setThemeUseCase.setTheme(theme);
    }

}
