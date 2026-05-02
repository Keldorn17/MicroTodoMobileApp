package hu.nje.todo.settings.domain.repository;

import hu.nje.todo.settings.domain.model.Theme;

public interface SettingsRepository {

    Theme getTheme();

    void setTheme(Theme theme);

}
