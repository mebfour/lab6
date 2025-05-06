package Classes;


public class Coordinates {
    private Long x; //Значение поля должно быть больше -326, Поле не может быть null
    private Integer y; //Значение поля должно быть больше -258, Поле не может быть null

    public Coordinates(Long x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {

    }

    public void setX(Long x) {
        this.x = x;
    }
    public void setY(Integer y) {
        this.y = y;
    }

    public Long getX() {
        return x;
    }
    public Integer getY() {
        return y;
    }



    @Override
    public String toString() {
        return "X: " + x +"; " + "Y: " + y;
    }
}

