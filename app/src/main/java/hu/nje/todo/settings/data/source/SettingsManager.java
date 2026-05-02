package hu.nje.todo.settings.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import hu.nje.todo.settings.domain.model.Theme;

@Singleton
public class SettingsManager {

    private static final String STORE_NAME = "ThemeState";
    private static final String KEY_NAME = "theme";

    private final SharedPreferences sharedPreferences;
    private final ReentrantLock preferencesLock = new ReentrantLock();

    @Inject
    public SettingsManager(@ApplicationContext Context context) {
        sharedPreferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }

    public Theme getTheme() {
        preferencesLock.lock();
        try {
            int themeIndex = sharedPreferences.getInt(KEY_NAME, 0);
            return switch (themeIndex) {
                case 1 -> Theme.LIGHT;
                case 2 -> Theme.DARK;
                default -> Theme.AUTO;
            };
        } finally {
            preferencesLock.unlock();
        }
    }

    public void saveTheme(Theme theme) {
        preferencesLock.lock();
        try {
            int themeIndex = switch (theme) {
                case AUTO -> 0;
                case LIGHT -> 1;
                case DARK -> 2;
            };
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_NAME, themeIndex);
            if (!editor.commit()) {
                throw new IllegalStateException("Failed to write state to shared prefs");
            }
        } finally {
            preferencesLock.unlock();
        }
    }

}
