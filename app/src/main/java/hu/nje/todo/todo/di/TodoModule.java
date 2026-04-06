package hu.nje.todo.todo.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.ZonedDateTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.nje.todo.core.data.AuthInterceptor;
import hu.nje.todo.todo.data.repository.StatisticsRepositoryImpl;
import hu.nje.todo.todo.data.repository.TodoRepositoryImpl;
import hu.nje.todo.todo.data.source.TodoApi;
import hu.nje.todo.todo.domain.json.ZonedDateTimeDeserializer;
import hu.nje.todo.todo.domain.json.ZonedDateTimeSerializer;
import hu.nje.todo.todo.domain.repository.StatisticsRepository;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class TodoModule {

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build();
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeSerializer())
                .create();
    }

    @Provides
    @Singleton
    public TodoApi provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl("https://todo.robottx.hu")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(TodoApi.class);
    }

    @Provides
    @Singleton
    public TodoRepository provideTodoRepository(TodoApi todoApi) {
        return new TodoRepositoryImpl(todoApi);
    }

    @Provides
    @Singleton
    public StatisticsRepository provideStatisticsRepository(TodoApi todoApi, @dagger.hilt.android.qualifiers.ApplicationContext android.content.Context context) {
        return new StatisticsRepositoryImpl(todoApi, context);
    }

}
