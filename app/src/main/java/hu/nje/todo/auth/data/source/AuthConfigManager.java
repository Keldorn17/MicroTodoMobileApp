package hu.nje.todo.auth.data.source;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import hu.nje.todo.R;
import hu.nje.todo.auth.domain.model.AuthConfig;

public class AuthConfigManager {

    private AuthConfig authConfig;

    public AuthConfigManager(Context context) {
        loadConfig(context);
    }

    @NonNull
    public AuthConfig getConfig() {
        return authConfig;
    }

    private void loadConfig(Context context) {
        try (Reader reader = new InputStreamReader(context.getResources().openRawResource(R.raw.auth_config))) {
            authConfig = new Gson().fromJson(reader, AuthConfig.class);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read auth config", ex);
        }
    }

}
