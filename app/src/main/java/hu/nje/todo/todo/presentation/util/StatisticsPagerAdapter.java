package hu.nje.todo.todo.presentation.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import hu.nje.todo.todo.presentation.fragment.BarChartFragment;
import hu.nje.todo.todo.presentation.fragment.PieChartFragment;

public class StatisticsPagerAdapter extends FragmentStateAdapter {

    public StatisticsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new PieChartFragment() : new BarChartFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
