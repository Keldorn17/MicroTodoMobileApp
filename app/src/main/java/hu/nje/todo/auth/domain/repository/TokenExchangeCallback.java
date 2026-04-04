package hu.nje.todo.auth.domain.repository;

public interface TokenExchangeCallback {
    void onResult(boolean isSuccess);
}
