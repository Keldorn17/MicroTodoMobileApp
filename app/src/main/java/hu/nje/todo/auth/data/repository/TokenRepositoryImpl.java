package hu.nje.todo.auth.data.repository;

import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import hu.nje.todo.auth.data.source.AuthStateManager;
import hu.nje.todo.auth.domain.repository.TokenRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

    private static final String TAG = "TokenRepositoryImpl";

    private final AuthorizationService authService;
    private final AuthStateManager authStateManager;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public String getAccessToken() {
        lock.lock();
        try {
            AuthState authState = authStateManager.getCurrentState();

            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<String> accessTokenReference = new AtomicReference<>();

            authState.performActionWithFreshTokens(authService, (accessToken, idToken, ex) -> {
                if (ex != null) {
                    Log.e(TAG, "getAccessToken: Failed to retrieve token", ex);
                } else {
                    accessTokenReference.set(accessToken);
                    authStateManager.replace(authState);
                }
                latch.countDown();
            });

            try {
                latch.await();
            } catch (InterruptedException ex) {
                Log.e(TAG, "Thread interrupted while waiting for token", ex);
                Thread.currentThread().interrupt();
            }

            return accessTokenReference.get();
        } finally {
            lock.unlock();
        }
    }

}
