package Classes;


public class Location {

    private float x;
    private Integer y; //Поле не может быть null
    private int z;
    private String name;

    public Location(float x, Integer y, int z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Location() {

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Название: " +this.getName()+'\n' + "Координаты: "+ "X: " + x +"; " + "Y: " + y +"; " + "Z: " + z ;
    }
}

