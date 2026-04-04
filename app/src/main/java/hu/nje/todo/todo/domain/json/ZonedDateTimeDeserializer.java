package hu.nje.todo.todo.domain.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

public class ZonedDateTimeDeserializer implements JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ZonedDateTime result = null;
        if (!json.isJsonNull()) {
            result = ZonedDateTime.parse(json.getAsString());
        }
        return result;
    }

}
