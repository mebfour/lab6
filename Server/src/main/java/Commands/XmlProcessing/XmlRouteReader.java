package Commands.XmlProcessing;



import Classes.Route;
import Collection.RouteCollectionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;



public class XmlRouteReader {

    public static RouteWrapper readRoutesFromXml (String filePath){
        try {
            JAXBContext context = JAXBContext.newInstance(RouteWrapper.class, Route.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (RouteWrapper) unmarshaller.unmarshal(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при чтении из XML");
            return new RouteWrapper();
        }
    }
    private static LinkedHashMap<String, Route> readDefaultFile() {
        try {
            File defaultFile = new File("file.xml");
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

