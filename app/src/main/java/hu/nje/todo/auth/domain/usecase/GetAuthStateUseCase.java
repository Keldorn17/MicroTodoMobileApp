package hu.nje.todo.auth.domain.usecase;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.nje.todo.auth.domain.repository.AuthRepository;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetAuthStateUseCase {

    private final AuthRepository authRepository;

    public boolean isAuthorized() {
        return authRepository.isAuthorized();
    }

}
