package hu.nje.todo.settings.domain.usecase;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.nje.todo.settings.domain.model.Theme;
import hu.nje.todo.settings.domain.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetThemeUseCase {

    private final SettingsRepository settingsRepository;

    public Theme getTheme() {
        return settingsRepository.getTheme();
    }

}
