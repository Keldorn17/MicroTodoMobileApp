package hu.nje.todo.auth.data.repository;

import android.content.Intent;
import android.net.Uri;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.EndSessionRequest;
import net.openid.appauth.ResponseTypeValues;

import java.util.HashMap;
import java.util.Map;

import hu.nje.todo.auth.data.source.AuthStateManager;
import hu.nje.todo.auth.domain.model.AuthConfig;
import hu.nje.todo.auth.domain.repository.AuthRepository;
import hu.nje.todo.auth.domain.repository.TokenExchangeCallback;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthStateManager authStateManager;
    private final AuthorizationService authorizationService;
    private final AuthConfig authConfig;

    public AuthRepositoryImpl(AuthStateManager authStateManager, AuthorizationService authorizationService, AuthConfig authConfig) {
        this.authStateManager = authStateManager;
        this.authorizationService = authorizationService;
        this.authConfig = authConfig;
    }

    @Override
    public Intent getAuthIntent() {
        AuthorizationServiceConfiguration config = getServiceConfiguration();
        AuthorizationRequest request = createAuthRequest(config);
        return authorizationService.getAuthorizationRequestIntent(request);
    }

    @Override
    public Intent getRegisterIntent() {
        AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
                Uri.parse(authConfig.getRegistrationEndpoint()),
                Uri.parse(authConfig.getTokenEndpoint())
        );
        AuthorizationRequest request = createAuthRequest(config);
        return authorizationService.getAuthorizationRequestIntent(request);
    }

    @Override
    public Intent getEndSessionIntent() {
        AuthState authState = authStateManager.getCurrentState();
        Map<String, String> additionalParameters = new HashMap<>();
        additionalParameters.put("client_id", authConfig.getClientId());
        EndSessionRequest request = new EndSessionRequest.Builder(getServiceConfiguration())
                .setIdTokenHint(authState.getIdToken())
                .setPostLogoutRedirectUri(Uri.parse(authConfig.getRedirectUri()))
                .setAdditionalParameters(additionalParameters)
                .build();
        return authorizationService.getEndSessionRequestIntent(request);
    }

    @Override
    public void exchangeCodeForToken(Intent data, TokenExchangeCallback callback) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(data);
        AuthorizationException ex = AuthorizationException.fromIntent(data);
        authStateManager.updateAfterAuthorization(response, ex);
        if (response != null) {
            authorizationService.performTokenRequest(
                    response.createTokenExchangeRequest(),
                    (tokenResponse, tokenEx) -> {
                        authStateManager.updateAfterTokenResponse(tokenResponse, tokenEx);
                        callback.onResult(tokenResponse != null);
                    }
            );
        } else {
            callback.onResult(false);
        }
    }

    @Override
    public boolean isAuthorized() {
        return authStateManager.getCurrentState().isAuthorized();
    }

    @Override
    public String getProfileEndpoint() {
        return authConfig.getProfileEndpoint();
    }

    @Override
    public void clearAuthState() {
        authStateManager.replace(new AuthState());
    }

    private AuthorizationServiceConfiguration getServiceConfiguration() {
        return new AuthorizationServiceConfiguration(
                Uri.parse(authConfig.getAuthEndpoint()),
                Uri.parse(authConfig.getTokenEndpoint()),
                null,
                Uri.parse(authConfig.getEndSessionEndpoint())
        );
    }

    private AuthorizationRequest createAuthRequest(AuthorizationServiceConfiguration config) {
        return new AuthorizationRequest.Builder(
                config,
                authConfig.getClientId(),
                ResponseTypeValues.CODE,
                Uri.parse(authConfig.getRedirectUri())
        ).setScope(authConfig.getScopes()).build();
    }

}
