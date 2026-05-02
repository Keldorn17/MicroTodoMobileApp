package hu.nje.todo.settings.presentation.util;

import androidx.appcompat.app.AppCompatDelegate;

import hu.nje.todo.settings.domain.model.Theme;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ThemeUtils {

    public static void applyTheme(Theme theme) {
        switch (theme) {
            case AUTO ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            case LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            case DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

}
