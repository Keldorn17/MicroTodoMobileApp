package hu.nje.todo.todo.presentation.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hu.nje.todo.todo.presentation.fragment.BarChartFragment;
import hu.nje.todo.todo.presentation.fragment.PieChartFragment;

public class StatisticsPagerAdapter extends FragmentStateAdapter {

    public enum StatisticsTab {
        PIE_CHARTS {
            @Override
            public Fragment createFragment() {
                return new PieChartFragment();
            }
        },
        BAR_CHARTS {
            @Override
            public Fragment createFragment() {
                return new BarChartFragment();
            }
        };

        public abstract Fragment createFragment();


        private static final List<StatisticsTab> TABS = Collections.unmodifiableList(Arrays.asList(values()));

        public static StatisticsTab getAt(int position) {
            return TABS.get(position);
        }

        public static int getCount() {
            return TABS.size();
        }
    }

    public StatisticsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StatisticsTab.getAt(position).createFragment();
    }

    @Override
    public int getItemCount() {
        return StatisticsTab.getCount();
    }
}