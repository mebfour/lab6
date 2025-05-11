package Collection;

import Classes.Route;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Класс для управления коллекцией маршрутов.
 * Инкапсулирует логику работы с данными.
 */

import Commands.XmlProcessing.RouteWrapper;
import Commands.XmlProcessing.XmlRouteReader;
import com.google.gson.Gson;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "routeList")
public class RouteCollectionManager {
    public static String globalFilePath = "D:/itmo/jaba/lab5ALLBLYT/mav3/src/main/java/files/file.xml";
    public static LinkedHashMap<String, Route> routeList = XmlRouteReader.readRoutesFromXml(globalFilePath).getRouteMap();
    private static Date initializationTime = new Date();
    private int currentMaxId = (int) routeList.values().stream()
                .mapToLong(Route::getId)
                .max()
                .orElse(0);
    public synchronized long generateNextId() {
        return ++currentMaxId;
    }

    public static void init(String path) {
        globalFilePath = path;
        routeList = XmlRouteReader.readRoutesFromXml(globalFilePath).getRouteMap();

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
        String filePath = globalFilePath;
        try {
            RouteWrapper wrapper = new RouteWrapper();
            wrapper.setRouteMap(routeList);
            wrapper.setInitializationTime(initializationTime);

            JAXBContext context = JAXBContext.newInstance(RouteWrapper.class, Route.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(wrapper, new File(filePath));
        } catch (Exception e) {

            System.err.println("Ошибка при сохранении в XML");
        }

    }
}
