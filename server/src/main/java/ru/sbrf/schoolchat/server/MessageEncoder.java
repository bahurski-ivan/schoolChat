package ru.sbrf.schoolchat.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.sbrf.schoolchat.objects.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 16/11/2016.
 */
public class MessageEncoder {
    static private final Map<String, Class<?>> typeMap = new HashMap<>();

    static {
        typeMap.put(LoginRequest.class.getSimpleName(), LoginRequest.class);
        typeMap.put(LoginResponse.class.getSimpleName(), LoginResponse.class);

        typeMap.put(ListMessagesRequest.class.getSimpleName(), ListMessagesRequest.class);
        typeMap.put(ListMessagesResponse.class.getSimpleName(), ListMessagesResponse.class);

        typeMap.put(SendTextRequest.class.getSimpleName(), SendTextRequest.class);
        typeMap.put(TextMessage.class.getSimpleName(), TextMessage.class);
    }

    private Gson gson = new Gson();

    public String encode(Object value) {
        String type = value.getClass().getSimpleName();

        JsonObject object = (JsonObject) gson.toJsonTree(value);
        object.addProperty("type", type);

        return object.toString();
    }

    public Object decode(String value) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(value).getAsJsonObject();

        JsonElement element = obj.get("type");

        if (element == null)
            return null;

        String type = element.getAsString();

        Class<?> clazz = typeMap.get(type);

        if (clazz == null)
            return null;

        return gson.fromJson(value, clazz);
    }
}
