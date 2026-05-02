package hu.nje.todo.settings.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.nje.todo.settings.data.repository.SettingsRepositoryImpl;
import hu.nje.todo.settings.data.source.SettingsManager;
import hu.nje.todo.settings.domain.repository.SettingsRepository;

@Module
@InstallIn(SingletonComponent.class)
public class SettingsModule {

    @Provides
    @Singleton
    public SettingsRepository provideSettingsRepository(SettingsManager settingsManager) {
        return new SettingsRepositoryImpl(settingsManager);
    }

}
