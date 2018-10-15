package de.openfiresource.openpager.dagger.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.openfiresource.openpager.models.AppDatabase;

@Module(includes = AppModule.class)
public class DatabaseModule {

    private final static String DATABASE = "database.db";

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE)
                .allowMainThreadQueries() // todo: Remove this as soon as possible
                .fallbackToDestructiveMigration()
                .build();
    }
}
