package hu.nje.todo.auth.domain.usecase;

import android.content.Intent;

import javax.inject.Inject;

import hu.nje.todo.auth.domain.repository.AuthRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetLoginIntentUseCase {

    private final AuthRepository repository;

    public Intent getIntent() {
        return repository.getAuthIntent();
    }

}
