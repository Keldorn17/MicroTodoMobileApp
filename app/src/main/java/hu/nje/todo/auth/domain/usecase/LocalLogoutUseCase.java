package hu.nje.todo.auth.domain.usecase;

import javax.inject.Inject;

import hu.nje.todo.auth.domain.repository.AuthRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LocalLogoutUseCase {

    private final AuthRepository authRepository;

    public void clearAuthState() {
        authRepository.clearAuthState();
    }

}
