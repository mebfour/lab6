package Classes;

import java.util.Date;
import javafx.beans.property.*;

public class Route {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Coordinates> coordinates = new SimpleObjectProperty<>();
    private final StringProperty owner = new SimpleStringProperty();
    private final ObjectProperty<Date> creationDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Location> from = new SimpleObjectProperty<>();
    private final ObjectProperty<Location> to = new SimpleObjectProperty<>();
    private final StringProperty key = new SimpleStringProperty();

    public Route() {
        this.creationDate.set(new Date());
    }

    public Route(int id, String name, Coordinates coordinates, Date creationDate, Location from, Location to) {
        this.id.set(id);
        this.name.set(name);
        this.coordinates.set(coordinates);
        this.creationDate.set(creationDate);
        this.from.set(from);
        this.to.set(to);
    }

    // Свойства для TableView

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<Coordinates> coordinatesProperty() {
        return coordinates;
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    public ObjectProperty<Date> creationDateProperty() {
        return creationDate;
    }

    public ObjectProperty<Location> fromProperty() {
        return from;
    }

    public ObjectProperty<Location> toProperty() {
        return to;
    }

    public StringProperty keyProperty() {
        return key;
    }

    // Геттеры и сеттеры для значений

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Coordinates getCoordinates() {
        return coordinates.get();
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates.set(coordinates);
    }

    public String getOwner() {
        return owner.get();
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public Date getCreationDate() {
        return creationDate.get();
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate.set(creationDate);
    }

    public Location getFrom() {
        return from.get();
    }

    public void setFrom(Location from) {
        this.from.set(from);
    }

    public Location getTo() {
        return to.get();
    }

    public void setTo(Location to) {
        this.to.set(to);
    }

    public String getKey() {
        return key.get();
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    @Override
    public String toString() {
        // Можно оставить ваш текущий toString или адаптировать под свойства
        return "Route{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", coordinates=" + getCoordinates() +
                ", owner=" + getOwner() +
                ", creationDate=" + getCreationDate() +
                ", from=" + getFrom() +
                ", to=" + getTo() +
                ", key=" + getKey() +
                '}';
    }
}
