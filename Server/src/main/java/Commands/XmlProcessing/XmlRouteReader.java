package Commands.XmlProcessing;

import Classes.Route;
import Collection.RouteCollectionManager;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.util.LinkedHashMap;

import static Collection.RouteCollectionManager.globalFilePath;


public class XmlRouteReader {

    public static RouteWrapper readRoutesFromXml (String filePath){
        try {
            JAXBContext context = JAXBContext.newInstance(RouteWrapper.class, Route.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            RouteWrapper wrapper = (RouteWrapper) unmarshaller.unmarshal(new File(filePath));

            if (wrapper.getInitializationTime() != null) {
                RouteCollectionManager.setInitializationTime(wrapper.getInitializationTime());
            }

            return wrapper;
        } catch (Exception e) {

            System.err.println("Ошибка при чтении из XML");

            return new RouteWrapper();
        }
    }
    private static LinkedHashMap<String, Route> readDefaultFile() {
        try {
            File defaultFile = new File(globalFilePath);
            if (defaultFile.exists()) {
                try (InputStream is = new FileInputStream(defaultFile)) {
                    JAXBContext context = JAXBContext.newInstance(RouteWrapper.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    RouteWrapper wrapper = (RouteWrapper) unmarshaller.unmarshal(is);
                    if (wrapper.getInitializationTime()!= null){
                        RouteCollectionManager.setInitializationTime(wrapper.getInitializationTime());
                    }
                    return wrapper.getRouteMap() != null ?
                            new LinkedHashMap<>(wrapper.getRouteMap()) :
                            new LinkedHashMap<>();
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения файла по умолчанию");
        }
        return new LinkedHashMap<>(); // Возвращаем пустую коллекцию
    }
}

