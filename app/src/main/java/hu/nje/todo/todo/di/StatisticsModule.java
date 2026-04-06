package hu.nje.todo.todo.di;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.nje.todo.todo.data.repository.StatisticsRepositoryImpl;
import hu.nje.todo.todo.domain.repository.StatisticsRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class StatisticsModule {
    @Binds
    public abstract StatisticsRepository bindStatisticsRepository(StatisticsRepositoryImpl impl);
}