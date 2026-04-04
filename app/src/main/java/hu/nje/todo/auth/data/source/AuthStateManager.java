package hu.nje.todo.auth.data.source;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.RegistrationResponse;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class AuthStateManager {

    private static final String TAG = "AuthStateManager";
    private static final String STORE_NAME = "AuthState";
    private static final String KEY_STATE = "state";

    private final SharedPreferences sharedPreferences;
    private final ReentrantLock preferencesLock = new ReentrantLock();
    private final AtomicReference<AuthState> currentAuthState = new AtomicReference<>();

    public AuthStateManager(@ApplicationContext Context context) {
        sharedPreferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    public AuthState getCurrentState() {
        AuthState authState;
        if (currentAuthState.get() != null) {
            authState = currentAuthState.get();
        } else {
            authState = readState();
            if (!currentAuthState.compareAndSet(null, authState)) {
                authState = currentAuthState.get();
            }
        }
        return authState;
    }

    @NonNull
    @AnyThread
    public AuthState updateAfterAuthorization(
            @Nullable AuthorizationResponse response,
            @Nullable AuthorizationException ex) {
        AuthState current = getCurrentState();
        current.update(response, ex);
        return replace(current);
    }

    @NonNull
    @AnyThread
    public AuthState updateAfterTokenResponse(
            @Nullable TokenResponse response,
            @Nullable AuthorizationException ex) {
        AuthState current = getCurrentState();
        current.update(response, ex);
        return replace(current);
    }

    @NonNull
    @AnyThread
    public AuthState updateAfterRegistration(
            RegistrationResponse response,
            AuthorizationException ex) {
        AuthState current = getCurrentState();
        if (ex == null) {
            current.update(response);
            replace(current);
        }
        return current;
    }

    @NonNull
    @AnyThread
    public AuthState replace(@NonNull AuthState state) {
        writeState(state);
        currentAuthState.set(state);
        return state;
    }

    @NonNull
    @AnyThread
    private AuthState readState() {
        preferencesLock.lock();
        AuthState authState;
        try {
            String currentState = sharedPreferences.getString(KEY_STATE, null);
            if (currentState != null) {
                try {
                    authState = AuthState.jsonDeserialize(currentState);
                } catch (JSONException exception) {
                    Log.w(TAG, "Failed to deserialize stored auth state - discarding");
                    authState = new AuthState();
                }
            } else {
                authState = new AuthState();
            }
        } finally {
            preferencesLock.unlock();
        }
        return authState;
    }

    @AnyThread
    private void writeState(@Nullable AuthState state) {
        preferencesLock.lock();
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (state == null) {
                editor.remove(KEY_STATE);
            } else {
                editor.putString(KEY_STATE, state.jsonSerializeString());
            }
            if (!editor.commit()) {
                throw new IllegalStateException("Failed to write state to shared prefs");
            }
        } finally {
            preferencesLock.unlock();
        }
    }

}
