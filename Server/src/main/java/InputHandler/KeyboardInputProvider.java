package InputHandler;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.SortedMap;

public class KeyboardInputProvider implements InputProvider {
    private final Scanner scanner = new Scanner(System.in);

    public KeyboardInputProvider() {}

    @Override
    public String readString(String prompt) {
        while (true) {
            System.out.println(prompt);
            String inpStr = scanner.nextLine().trim();
            if (!inpStr.isEmpty()) {
                return inpStr;
            }else {
                System.out.println("Должны содержаться какие-нибудь символы, попробуйте еще раз");
            }
        }
    }

    @Override
    public int readInt(String prompt, int minValue) {
        while (true) {
            try {
                System.out.println(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < minValue) {
                    System.out.println("Значение должно быть не меньше " + minValue);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число.");
            }
        }
    }

    @Override
    public long readLong(String prompt, long minValue) {
        while (true) {
            try {
                System.out.println(prompt);
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value < minValue) {
                    System.out.println("Значение должно быть не меньше " + minValue);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число.");
            }
        }
    }

    @Override
    public float readFloat(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                return Float.parseFloat(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести число.");
            }
        }
    }

    @Override
    public double readDouble(String prompt, int minValue) {
        while (true) {
            try {
                System.out.println(prompt);
                String input = scanner.nextLine().trim();
                BigDecimal decimal = new BigDecimal(input);

                // Сравниваем с minValue
                if (decimal.compareTo(BigDecimal.valueOf(minValue)) > 0) {
                    return decimal.doubleValue(); // Возвращаем double (если точность не критична)
                }else {
                    System.out.println("Число должно быть больше " + minValue + ", повторите попытку");
                }
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести число.");
            }
        }
    }
}