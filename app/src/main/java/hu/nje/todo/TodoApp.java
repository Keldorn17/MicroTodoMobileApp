package hu.nje.todo;

import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import hu.nje.todo.settings.domain.usecase.GetThemeUseCase;
import hu.nje.todo.settings.presentation.util.ThemeUtils;

@HiltAndroidApp
public class TodoApp extends Application {

    @Inject
    GetThemeUseCase getThemeUseCase;

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeUtils.applyTheme(getThemeUseCase.getTheme());
    }

}
