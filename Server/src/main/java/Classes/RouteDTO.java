package Classes;

import java.util.Date;

public class RouteDTO {
    public RouteDTO(int id, String name, long x, int y, String owner, long creationDate, float fromX, int fromY, int fromZ, String fromName,  float toX, int toY, int toZ, String toName, String key) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.creationDate = creationDate;
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromZ = fromZ;
        this.fromName = fromName;
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
        this.toName = toName;
        this.key = key;
    }

    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long x;
    private int y;
    private String owner;
    private long creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float fromX;
    private int fromY;
    private int fromZ;
    private String fromName;
    private float toX;
    private int toY;
    private int toZ;
    private String toName;
    private String key;
}
