package xyz.kymirai.translator;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

class ValueConverter {
    @TypeConverter
    public HashMap<String, Star.Value[]> revertValue(String value) {
        try {
            return new Gson().fromJson(value, new TypeToken<HashMap<String, Star.Value[]>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public String converterValue(HashMap<String, Star.Value[]> value) {
        return new Gson().toJson(value);
    }
}