package Commands.XmlProcessing;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Date;
import java.util.LinkedHashMap;
import Classes.Route;

import static Collection.RouteCollectionManager.routeList;


@XmlRootElement(name = "routes") // Указываем, что это корневой элемент XML
@XmlAccessorType(XmlAccessType.PROPERTY)
public class RouteWrapper {
    private Date initializationTime;
    private LinkedHashMap<String, Route> routeMap = routeList;

    @XmlElement(name = "initializationTime")
    public Date getInitializationTime() {
        return initializationTime;
    }
    public void setInitializationTime(Date time) {
        this.initializationTime = time;
    }

    @XmlElement(name = "route") // Указываем имя элемента в XML
    public LinkedHashMap<String, Route> getRouteMap() {
        return routeMap;
    }

    public void setRouteMap(LinkedHashMap<String, Route> routeMap) {
        this.routeMap = routeMap;
    }


    // Конструктор по умолчанию (обязателен для JAXB)
    public RouteWrapper() {
        routeMap = new LinkedHashMap<>();
    }

    // Конструктор с параметрами
    public RouteWrapper(LinkedHashMap<String, Route> routeMap) {
        this.routeMap = routeMap;
    }

    // Геттер и сеттер для коллекции







}
