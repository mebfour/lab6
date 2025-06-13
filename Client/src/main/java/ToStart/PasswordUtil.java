package ToStart;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
    /**
     *
     * @param password исходный пароль в виде строки
     * @return шестнадцатеричное представление хэша пароля
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-384"); //создаёт объект, который умеет вычислять хэш с использованием алгоритма SHA-384.
        byte[] by = password.getBytes(StandardCharsets.UTF_8);   //  преобразует в байты
        byte[] hashBytes = md.digest(by);    // вычисляет хэш
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));    //Для каждого байта b из массива hashBytes вызывается String.format("%02x", b), который преобразует байт в двухсимвольное шестнадцатеричное представление
        }
        return sb.toString();
    }
}

