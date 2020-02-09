package xyz.kymirai.translator;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface StarDao {
    @Query("SELECT * FROM star WHERE text == :text")
    Star[] get(String text);

    @Query("SELECT * FROM star")
    Star[] getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Star... stars);

    @Query("DELETE FROM star WHERE text == :text")
    void delete(String text);
}
