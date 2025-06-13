package Classes;
import java.util.Date;


public class Route {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private String owner;
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from; //Поле не может быть null
    private Location to; //Поле может быть null
    private String key;

    public Route() {}

    public Route(int id, String name, Coordinates coordinates, Date creationDate, Location from, Location to) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
    }

    public Route(String name, Coordinates coordinates, Location from, Location to) {
        this.name = name;
        this.coordinates = coordinates;
        this.from = from;
        this.to = to;
    }



    public void setKey(String key) {
        this.key = key;
    }
    public String getKey(){
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public String getOwner(){return owner;}
    public void setOwner(String owner){this.owner = owner;}


    {
        this.creationDate = new Date();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        String info = "";
        info += "\n Ключ: " + key;
        info += "\n Имя: " + name;
        info += "\n Координаты: " + coordinates;
        String date = creationDate.toString();
        info += '\n'+"Добавлен "+date.substring(0, 10)+" "+date.substring(11, 19)+'\n';
        info += "\n Куда " + to;
        info += "\n Откуда " + from;
        info += "\n Создал " + owner;
        info += "\n id: " + id+ '\n' + "=========================================";;
        return info;
    }



}

