package hu.nje.todo.todo.domain.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer implements JsonSerializer<ZonedDateTime> {

    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement result = null;
        if (src != null) {
            result = new JsonPrimitive(src.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
        return result;
    }

}
