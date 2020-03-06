package xyz.kymirai.translator.bean;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.HashMap;

@Entity(tableName = "star")
public class Star {
    public static class Value {
        public String text;
        public String tip;

        public Value(String text, String tip) {
            this.text = text;
            this.tip = tip;
        }
    }

    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String text;

    public HashMap<String, Value[]> values;

    public Star(@NonNull String text, HashMap<String, Value[]> values) {
        this.text = text;
        this.values = values;
    }

    @Ignore
    public Star(@NonNull String text) {
        this.text = text;
        this.values = new HashMap<>();
    }
}
