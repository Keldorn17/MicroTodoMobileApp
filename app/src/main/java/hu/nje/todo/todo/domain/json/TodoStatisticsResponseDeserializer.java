package hu.nje.todo.todo.domain.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import hu.nje.todo.todo.domain.model.TodoStatisticsEntryResponse;
import hu.nje.todo.todo.domain.model.TodoStatisticsResponse;

public class TodoStatisticsResponseDeserializer implements JsonDeserializer<TodoStatisticsResponse> {

    @Override
    public TodoStatisticsResponse deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Expected JsonObject for TodoStatisticsResponse");
        }
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("total") && jsonObject.get("total").isJsonPrimitive()) {
            TodoStatisticsResponse response = new TodoStatisticsResponse();
            if (jsonObject.has("total")) {
                response.setTotal(jsonObject.get("total").getAsLong());
            }
            if (jsonObject.has("finished")) {
                response.setFinished(jsonObject.get("finished").getAsLong());
            }
            if (jsonObject.has("unfinished")) {
                response.setUnfinished(jsonObject.get("unfinished").getAsLong());
            }
            if (jsonObject.has("statistics") && jsonObject.get("statistics").isJsonObject()) {
                Map<String, TodoStatisticsEntryResponse> statsMap = context.deserialize(
                        jsonObject.get("statistics"),
                        new TypeToken<Map<String, TodoStatisticsEntryResponse>>() {}.getType()
                );
                response.setStatistics(statsMap);
            }
            return response;
        } else {
            Map<String, TodoStatisticsEntryResponse> statistics = context.deserialize(
                    json,
                    new TypeToken<Map<String, TodoStatisticsEntryResponse>>() {}.getType()
            );
            return TodoStatisticsResponse.builder()
                    .statistics(statistics)
                    .build();
        }
    }

}
