package Classes;

import paint.MyBoundingBox;

public class RouteDTO {

    private int id;
    private String name;
    private long x;
    private int y;
    private String owner;
    private long creationDate;
    private float fromX;
    private int fromY;
    private int fromZ;
    private String fromName;
    private float toX;
    private int toY;
    private int toZ;
    private String toName;
    private String key;
    private MyBoundingBox myBoundingBox;


    public String getByKey(String key) {
        return switch (key) {
            case "fromX" -> String.valueOf(fromX);
            case "fromY" -> String.valueOf(fromY);
            case "fromZ" -> String.valueOf(fromZ);
            case "fromName" -> fromName;
            case "toX" -> String.valueOf(toX);
            case "toY" -> String.valueOf(toY);
            case "toZ" -> String.valueOf(toZ);
            case "toName" -> toName;
            default -> "";
        };
    }
        // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public float getFromX() {
        return fromX;
    }

    public void setFromX(float fromX) {
        this.fromX = fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getFromZ() {
        return fromZ;
    }

    public void setFromZ(int fromZ) {
        this.fromZ = fromZ;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public float getToX() {
        return toX;
    }

    public void setToX(float toX) {
        this.toX = toX;
    }

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }

    public int getToZ() {
        return toZ;
    }

    public void setToZ(int toZ) {
        this.toZ = toZ;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MyBoundingBox getBoundingBox() {
        return myBoundingBox;
    }

    public void setBoundingBox(MyBoundingBox myBoundingBox) {
        this.myBoundingBox = myBoundingBox;
    }
}