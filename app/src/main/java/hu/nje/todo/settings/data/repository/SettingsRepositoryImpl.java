package hu.nje.todo.settings.data.repository;

import hu.nje.todo.settings.data.source.SettingsManager;
import hu.nje.todo.settings.domain.model.Theme;
import hu.nje.todo.settings.domain.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettingsRepositoryImpl implements SettingsRepository {

    private final SettingsManager settingsManager;

    @Override
    public Theme getTheme() {
        return settingsManager.getTheme();
    }

    @Override
    public void setTheme(Theme theme) {
        settingsManager.saveTheme(theme);
    }

}
