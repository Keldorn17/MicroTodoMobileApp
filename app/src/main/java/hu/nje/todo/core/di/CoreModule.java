package hu.nje.todo.core.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.nje.todo.auth.domain.repository.TokenRepository;
import hu.nje.todo.core.data.AuthInterceptor;

@Module
@InstallIn(SingletonComponent.class)
public class CoreModule {

    @Provides
    @Singleton
    public AuthInterceptor provideAuthInterceptor(TokenRepository tokenRepository) {
        return new AuthInterceptor(tokenRepository);
    }

}
