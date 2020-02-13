package xyz.kymirai.translator.utills;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import xyz.kymirai.translator.bean.Star;
import xyz.kymirai.translator.dao.StarDao;

@Database(entities = {Star.class}, version = 1,exportSchema = false)
@TypeConverters(ValueConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StarDao starDao();
}
