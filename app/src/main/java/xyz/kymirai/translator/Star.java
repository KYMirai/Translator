package xyz.kymirai.translator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "star")
public class Star {
    static class Value {
        String text;
        String tip;

        Value(String text, String tip) {
            this.text = text;
            this.tip = tip;
        }
    }

    @PrimaryKey
    @ColumnInfo
    @NonNull
    String text;

    HashMap<String, Value[]> values;

    Star(@NonNull String text, HashMap<String, Value[]> values) {
        this.text = text;
        this.values = values;
    }
}
