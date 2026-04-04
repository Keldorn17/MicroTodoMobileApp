package hu.nje.todo.auth.domain.usecase;

import android.net.Uri;

import javax.inject.Inject;

import hu.nje.todo.auth.domain.repository.AuthRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetProfileUrlUseCase {

    private final AuthRepository authRepository;

    public Uri getProfileEndpoint() {
        return Uri.parse(authRepository.getProfileEndpoint());
    }

}
