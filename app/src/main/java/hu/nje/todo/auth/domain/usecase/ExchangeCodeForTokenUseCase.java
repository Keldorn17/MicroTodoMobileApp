package hu.nje.todo.auth.domain.usecase;

import android.content.Intent;

import javax.inject.Inject;

import hu.nje.todo.auth.domain.repository.AuthRepository;
import hu.nje.todo.auth.domain.repository.TokenExchangeCallback;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ExchangeCodeForTokenUseCase {

    private final AuthRepository repository;

    public void execute(Intent data, TokenExchangeCallback tokenExchangeCallback) {
        repository.exchangeCodeForToken(data, tokenExchangeCallback);
    }

}
