package InputHandler;

public interface InputProvider {

    String readString(String prompt);
    int readInt(String prompt, int minValue);
    long readLong(String prompt, long minValue);
    float readFloat(String prompt);
    double readDouble(String prompt, int minValue);
}




