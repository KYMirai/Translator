package xyz.kymirai.translator;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Star.class}, version = 1,exportSchema = false)
@TypeConverters(ValueConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StarDao starDao();
}
