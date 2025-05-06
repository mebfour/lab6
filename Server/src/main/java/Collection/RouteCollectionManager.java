package Collection;

import Classes.Route;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Класс для управления коллекцией маршрутов.
 * Инкапсулирует логику работы с данными.
 */

import Commands.XmlProcessing.XmlRouteReader;
import com.google.gson.Gson;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "routeList")
public class RouteCollectionManager {
    public transient static String globalFilePath = "D:/itmo/jaba/lab6/Server/src/main/java/file.xml";
    public static LinkedHashMap<String, Route> routeList = XmlRouteReader.readRoutesFromXml(globalFilePath);
    private static Date initializationTime;




    public static void init(String path) {
        globalFilePath = path;
        initializationTime = new Date();
        routeList = XmlRouteReader.readRoutesFromXml(globalFilePath);
        setInitializationTime(new Date());
    }

    public static Date getInitializationTime() {
        return initializationTime;
    }

    public static void setInitializationTime(Date date) {
        if (date != null) {
            initializationTime = date;
        }
    }

    public void addToCollection(Route route){
        try {
            routeList.put(route.getKey(), route);
        }catch (Exception e){
            routeList.put(route.getName(),route);
        }

    }
    
    public static Class<?> getCollectionType(){
        return routeList.getClass();
    }

    @XmlElement(name="route")
    public LinkedHashMap getCollection() {
        return routeList;
    }


    public void saveToFile() {
        try (Writer writer = new FileWriter(globalFilePath)) {

            new Gson().toJson(this, writer);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения изменений в файл");
        }
    }
}
