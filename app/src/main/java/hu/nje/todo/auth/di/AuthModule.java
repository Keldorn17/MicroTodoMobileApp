package hu.nje.todo.auth.di;

import android.content.Context;

import net.openid.appauth.AuthorizationService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import hu.nje.todo.auth.data.repository.AuthRepositoryImpl;
import hu.nje.todo.auth.data.repository.TokenRepositoryImpl;
import hu.nje.todo.auth.data.source.AuthConfigManager;
import hu.nje.todo.auth.data.source.AuthStateManager;
import hu.nje.todo.auth.domain.repository.AuthRepository;
import hu.nje.todo.auth.domain.repository.TokenRepository;

@Module
@InstallIn(SingletonComponent.class)
public class AuthModule {

    @Provides
    @Singleton
    public AuthStateManager provideAuthStateManager(@ApplicationContext Context context) {
        return new AuthStateManager(context);
    }

    @Provides
    @Singleton
    public AuthorizationService provideAuthorizationService(@ApplicationContext Context context) {
        return new AuthorizationService(context);
    }

    @Provides
    @Singleton
    public AuthConfigManager provideAuthConfigManager(@ApplicationContext Context context) {
        return new AuthConfigManager(context);
    }

    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(AuthStateManager authStateManager,
                                                AuthorizationService authorizationService,
                                                AuthConfigManager authConfigManager) {
        return new AuthRepositoryImpl(authStateManager,
                authorizationService,
                authConfigManager.getConfig());
    }

    @Provides
    @Singleton
    public TokenRepository provideTokenRepository(AuthorizationService authorizationService,
                                                  AuthStateManager authStateManager) {
        return new TokenRepositoryImpl(authorizationService, authStateManager);
    }

}
