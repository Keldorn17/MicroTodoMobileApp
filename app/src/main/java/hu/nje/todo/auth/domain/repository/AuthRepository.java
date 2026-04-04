package hu.nje.todo.auth.domain.repository;

import android.content.Intent;

public interface AuthRepository {

    Intent getAuthIntent();

    Intent getRegisterIntent();

    Intent getEndSessionIntent();

    void exchangeCodeForToken(Intent data, TokenExchangeCallback callback);

    boolean isAuthorized();

    String getProfileEndpoint();

    void clearAuthState();

}
