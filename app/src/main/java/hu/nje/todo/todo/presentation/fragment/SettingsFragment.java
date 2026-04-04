package hu.nje.todo.todo.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.openid.appauth.AuthorizationException;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.auth.presentation.LoginActivity;
import hu.nje.todo.databinding.FragmentSettingsBinding;
import hu.nje.todo.todo.presentation.viewmodel.SettingsViewModel;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    private final ActivityResultLauncher<Intent> logoutLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    AuthorizationException exception = AuthorizationException.fromIntent(result.getData());
                    if (exception != null) {
                        Log.e("SettingsFragment", ": Logout failed with error", exception);
                    }
                }
                viewModel.performLocalLogout();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding.profileBtn.setOnClickListener(v -> {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(false)
                    .build();
            customTabsIntent.launchUrl(requireContext(), viewModel.getProfileEndpoint());
        });
        binding.logoutBtn.setOnClickListener(v -> {
            Intent logoutIntent = viewModel.getLogoutIntent();
            logoutLauncher.launch(logoutIntent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel = null;
    }

}