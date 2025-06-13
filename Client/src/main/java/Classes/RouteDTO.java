package Classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

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


//    public RouteDTO(int id, String name, long x, int y, String owner, long creationDate, float fromX, int fromY, int fromZ, String fromName, float toX, int toY, int toZ, String toName, String key) {
//        this.id = new SimpleIntegerProperty(id);
//        this.name = new SimpleStringProperty(name);
//        this.x = new SimpleLongProperty(x);
//        this.y = new SimpleIntegerProperty(y);
//        this.owner = new SimpleStringProperty(owner);
//        this.creationDate = new SimpleLongProperty(creationDate);
//        this.fromX = new SimpleFloatProperty(fromX);
//        this.fromY = new SimpleIntegerProperty(fromY);
//        this.fromZ = new SimpleIntegerProperty(fromZ);
//        this.fromName = new SimpleStringProperty(fromName);
//        this.toX = new SimpleFloatProperty(toX);
//        this.toY = new SimpleIntegerProperty(toY);
//        this.toZ = new SimpleIntegerProperty(toZ);
//        this.toName = new SimpleStringProperty(toName);
//        this.key = new SimpleStringProperty(key);
//    }

//    private IntegerProperty id;
//    private StringProperty name;
//    private LongProperty x;
//    private IntegerProperty y;
//    private StringProperty owner;
//    private LongProperty creationDate;
//    private FloatProperty fromX;
//    private IntegerProperty fromY;
//    private IntegerProperty fromZ;
//    private StringProperty fromName;
//    private FloatProperty toX;
//    private IntegerProperty toY;
//    private IntegerProperty toZ;
//    private StringProperty toName;
//    private StringProperty key;

    // Property accessors
//    public IntegerProperty idProperty() {
//        return id;
//    }
//
//    public StringProperty nameProperty() {
//        return name;
//    }
//
//    public LongProperty xProperty() {
//        return x;
//    }
//
//    public IntegerProperty yProperty() {
//        return y;
//    }
//
//    public StringProperty ownerProperty() {
//        return owner;
//    }
//
//    public LongProperty creationDateProperty() {
//        return creationDate;
//    }
//
//    public FloatProperty fromXProperty() {
//        return fromX;
//    }
//
//    public IntegerProperty fromYProperty() {
//        return fromY;
//    }
//
//    public IntegerProperty fromZProperty() {
//        return fromZ;
//    }
//
//    public StringProperty fromNameProperty() {
//        return fromName;
//    }
//
//    public FloatProperty toXProperty() {
//        return toX;
//    }
//
//    public IntegerProperty toYProperty() {
//        return toY;
//    }
//
//    public IntegerProperty toZProperty() {
//        return toZ;
//    }
//
//    public StringProperty toNameProperty() {
//        return toName;
//    }
//
//    public StringProperty keyProperty() {
//        return key;
//    }
//
//    // Regular getters
//    public int getId() {
//        return id.get();
//    }
//
//    public String getName() {
//        return name.get();
//    }
//
//    public long getX() {
//        return x.get();
//    }
//
//    public int getY() {
//        return y.get();
//    }
//
//    public String getOwner() {
//        return owner.get();
//    }
//
//    public long getCreationDate() {
//        return creationDate.get();
//    }
//
//    public float getFromX() {
//        return fromX.get();
//    }
//
//    public int getFromY() {
//        return fromY.get();
//    }
//
//    public int getFromZ() {
//        return fromZ.get();
//    }
//
//    public String getFromName() {
//        return fromName.get();
//    }
//
//    public float getToX() {
//        return toX.get();
//    }
//
//    public int getToY() {
//        return toY.get();
//    }
//
//    public int getToZ() {
//        return toZ.get();
//    }
//
//    public String getToName() {
//        return toName.get();
//    }
//
//    public String getKey() {
//        return key.get();
//    }
//
//    public void setId(int id) {
//        this.id.set(id);
//    }
//
//    public void setName(String name) {
//        this.name.set(name);
//    }
//
//    public void setX(long x) {
//        this.x.set(x);
//    }
//
//    public void setY(int y) {
//        this.y.set(y);
//    }
//
//    public void setOwner(String owner) {
//        this.owner.set(owner);
//    }
//
//    public void setCreationDate(long creationDate) {
//        this.creationDate.set(creationDate);
//    }
//
//    public void setFromX(float fromX) {
//        this.fromX.set(fromX);
//    }
//
//    public void setFromY(int fromY) {
//        this.fromY.set(fromY);
//    }
//
//    public void setFromZ(int fromZ) {
//        this.fromZ.set(fromZ);
//    }
//
//    public void setFromName(String fromName) {
//        this.fromName.set(fromName);
//    }
//
//    public void setToX(float toX) {
//        this.toX.set(toX);
//    }
//
//    public void setToY(int toY) {
//        this.toY.set(toY);
//    }
//
//    public void setToZ(int toZ) {
//        this.toZ.set(toZ);
//    }
//
//    public void setToName(String toName) {
//        this.toName.set(toName);
//    }
//
//    public void setKey(String key) {
//        this.key.set(key);
//    }
}