package ToStart;

import com.google.gson.Gson;
import com.sun.javafx.collections.MappingChange;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RegisterController {
    private final Gson gson;
    private final Consumer<String> sendMessage;
    private VBox root = new VBox(10); // Корневой контейнер с отступами

    public RegisterController(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;
    }
    public Parent getView() {
        return root;
    }
    public void sendRegisterRequest(String username, String password) {
        try {
            String hashedPassword = PasswordUtil.hashPassword(password);
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", hashedPassword);

            String jsonParams = gson.toJson(params);
            CommandRequest commandRequest = new CommandRequest("register", jsonParams, username);
            String jsonRequest = gson.toJson(commandRequest);

            sendMessage.accept(jsonRequest);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ошибка хэширования пароля");
        }
    }

}
