package hu.nje.todo.core.data;

import androidx.annotation.NonNull;

import java.io.IOException;

import hu.nje.todo.auth.domain.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@RequiredArgsConstructor
public class AuthInterceptor implements Interceptor {

    private final TokenRepository repository;

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        String accessToken = repository.getAccessToken();
        requestBuilder.addHeader("Authorization", "Bearer " + accessToken);
        return chain.proceed(requestBuilder.build());
    }

}
