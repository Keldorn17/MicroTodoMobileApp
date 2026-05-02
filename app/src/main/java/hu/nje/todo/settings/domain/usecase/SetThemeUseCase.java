package hu.nje.todo.settings.domain.usecase;

import javax.inject.Inject;

import hu.nje.todo.settings.domain.model.Theme;
import hu.nje.todo.settings.domain.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SetThemeUseCase {

    private final SettingsRepository settingsRepository;

    public void setTheme(Theme theme) {
        settingsRepository.setTheme(theme);
    }

}
