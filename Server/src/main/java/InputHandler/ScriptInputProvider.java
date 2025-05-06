package InputHandler;

import java.io.BufferedReader;
import java.io.IOException;

public class ScriptInputProvider implements InputProvider {
    private final BufferedReader reader;

    public ScriptInputProvider(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public String readString(String prompt) {
        try {
            return reader.readLine().trim();
        } catch (IOException e) {
            System.out.println("");
            throw new RuntimeException("Ошибка при чтении строки из скрипта", e);
        }
    }

    @Override
    public int readInt(String prompt, int minValue) {
        try {
            int value = Integer.parseInt(reader.readLine().trim());
            if (value < minValue) {
                throw new IllegalArgumentException("Значение должно быть не меньше " + minValue);
            }
            return value;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении числа из скрипта", e);
        }
    }

    @Override
    public long readLong(String prompt, long minValue) {
        try {
            long value = Long.parseLong(reader.readLine().trim());
            if (value < minValue) {
                try {
                    throw new IllegalArgumentException("Значение должно быть не меньше " + minValue);
                }catch (IllegalArgumentException e){
                    System.out.println(e.getMessage());
                }
            }
            return value;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении числа из скрипта", e);
        }
    }

    @Override
    public float readFloat(String prompt) {
        try {
            return Float.parseFloat(reader.readLine().trim());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении числа из скрипта", e);
        }
    }

    @Override
    public double readDouble(String prompt, int minValue) throws IllegalArgumentException {
        try {
            System.out.println(prompt);
            double inp = Double.parseDouble(reader.readLine().trim());

            if (inp <= minValue) {
                System.out.println("Значение должно быть больше " + minValue);
                throw new IllegalArgumentException("Некорректное расстояние");
            }
            return inp;

        } catch (NumberFormatException e) {
            System.out.println("Это не число");
            //throw new IllegalArgumentException("Это не число");
        } catch (IOException e ) {
            //throw new RuntimeException("Ошибка сохранения в файл");
        } catch (IllegalArgumentException e){
            System.out.println("Некорректные значения в файле");
            //throw new IllegalArgumentException("Плохие значения");
        }
        return -1;
    }
}