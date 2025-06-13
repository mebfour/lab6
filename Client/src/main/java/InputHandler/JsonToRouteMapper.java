package InputHandler;

import Classes.RouteDTO;
import ToStart.ClientNetworkManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import static ToStart.ClientNetworkManager.routeResponse;

public class JsonToRouteMapper {

    public static Map<String, RouteDTO> parseJsonToRouteMap(String jsonArgs) {
        jsonArgs = waitForJsonResponse();
        if (jsonArgs == null || jsonArgs.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON строка пустая");
        }

        // Защита: если это не JSON, а текст — выкидываем осмысленную ошибку
        if (!jsonArgs.trim().startsWith("{") && !jsonArgs.trim().startsWith("\"{")) {
            System.err.println("Ошибка: Ожидался JSON, но получена строка:");
            System.err.println(jsonArgs);
            throw new IllegalArgumentException("Неверный формат данных: не JSON");
        }

        Gson lenientGson = new GsonBuilder().setLenient().create();

        try {
            String innerJson;
            if (jsonArgs.trim().startsWith("\"")) {
                innerJson = lenientGson.fromJson(jsonArgs, String.class);
            } else {
                innerJson = jsonArgs;
            }

            return lenientGson.fromJson(innerJson, new TypeToken<Map<String, RouteDTO>>(){}.getType());
        } catch (Exception e) {
            System.err.println("Ошибка парсинга JSON:");
            System.err.println("Входная строка: " + jsonArgs);
            throw e;
        }
    }
    public static String waitForJsonResponse() {
        String jsonArgs = null;

        // Ждём, пока jsonArgs не станет непустым
        while (jsonArgs == null || jsonArgs.trim().isEmpty()) {
            jsonArgs = routeResponse.getMessage();

            if (jsonArgs == null || jsonArgs.trim().isEmpty()) {
                try {
                    Thread.sleep(100); // подождать 100 мс перед следующей попыткой
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Ожидание прервано");
                    return null;
                }
            }
        }

        return jsonArgs;
    }
}
