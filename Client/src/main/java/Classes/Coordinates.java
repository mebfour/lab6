package Classes;


import javafx.beans.property.*;

public class Coordinates {
    private final LongProperty x = new SimpleLongProperty();
    private final IntegerProperty y = new SimpleIntegerProperty(); //Значение поля должно быть больше -258, Поле не может быть null

    public Coordinates(Long x, Integer y) {
        this.x.set(x);
        this.y.set(y);
    }
    public Coordinates() {}

    public long getX() { return x.get(); }
    public void setX(long value) { x.set(value); }
    public LongProperty xProperty() { return x; }

    // --- Y ---
    public int getY() { return y.get(); }
    public void setY(int value) { y.set(value); }
    public IntegerProperty yProperty() { return y; }



    @Override
    public String toString() {
        return "X: " + x +"; " + "Y: " + y;
    }
}

