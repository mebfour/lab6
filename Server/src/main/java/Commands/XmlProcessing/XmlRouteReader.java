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

    public static LinkedHashMap<String, Route> readRoutesFromXml (String filePath){
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<LinkedHashMap<String, Route>>(){}.getType();
            LinkedHashMap<String, Route> routeMap = new Gson().fromJson(reader, type);
            if (routeMap == null) return new LinkedHashMap<>(); // если файл пустой
            return routeMap;
        } catch (Exception e) {
            System.err.println("Ошибка чтения коллекции из JSON: " + e.getMessage());
            return new LinkedHashMap<>();
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

