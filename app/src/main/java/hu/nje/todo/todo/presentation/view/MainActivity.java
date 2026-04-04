package hu.nje.todo.todo.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.R;
import hu.nje.todo.auth.presentation.LoginActivity;
import hu.nje.todo.databinding.ActivityMainBinding;
import hu.nje.todo.todo.presentation.viewmodel.MainViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.isAuthorized().observe(this, isAuthorized -> {
            if (Boolean.TRUE.equals(isAuthorized)) {
                setupView();
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
            binding.fabAddTodo.setOnClickListener(v -> {
                navController.navigate(R.id.todoEditorFragment);
            });
            navController.addOnDestinationChangedListener(((controller, destination, bundle) -> {
                int destinationId = destination.getId();
                binding.fabAddTodo.setVisibility(getShowButtonDestinations().contains(destinationId) ? View.VISIBLE : View.GONE);
                binding.bottomNavigationView.setVisibility(getShowNavbarDestinations().contains(destinationId) ? View.VISIBLE : View.GONE);
            }));
        }
    }

    private Set<Integer> getShowButtonDestinations() {
        return Set.of(
                R.id.ownTodoListFragment,
                R.id.sharedTodoListFragment,
                R.id.allTodoListFragment
        );
    }

    private Set<Integer> getShowNavbarDestinations() {
        return Set.of(
                R.id.ownTodoListFragment,
                R.id.sharedTodoListFragment,
                R.id.allTodoListFragment,
                R.id.todoStatisticsFragment,
                R.id.settingsFragment
        );
    }

}