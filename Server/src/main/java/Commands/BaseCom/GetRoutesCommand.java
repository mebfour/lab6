package Commands.BaseCom;

import Classes.Route;
import Classes.RouteDTO;
import Collection.RouteCollectionManager;
import Commands.BDProcessing.BDReader;
import Commands.Command;
import Commands.CommandResponse;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static Collection.RouteCollectionManager.routeList;


public class GetRoutesCommand implements Command {

    Gson gson = new Gson();


    @Override
    public CommandResponse execute(String args) {
        Map<String, Route> routeMap = BDReader.readRoutesFromBd().getRouteMap();
        if (!routeMap.isEmpty()) {
            String json = gson.toJson(convertToDTOs(routeMap));
            System.out.println("from BD: " + json);
            return new CommandResponse(json, true);
        } else {
            return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
        }
    }


    public Map<String, RouteDTO> convertToDTOs(Map<String, Route> routes) {
        Map<String, RouteDTO> dtos = new HashMap<>();
        for (Map.Entry<String, Route> entry : routes.entrySet()) {
            dtos.put(entry.getKey(), convertToDTO(entry.getValue()));
        }
        return dtos;
    }

    public static RouteDTO convertToDTO(Route route) {
        RouteDTO dto = new RouteDTO(
                route.getId(),
                route.getName(),
                route.getCoordinates().getX(),
                route.getCoordinates().getY(),
                route.getOwner(),
                route.getCreationDate().getTime(),
                route.getFrom().getX(),
                route.getFrom().getY(),
                route.getFrom().getZ(),
                route.getFrom().getName(),
                route.getTo().getX(),
                route.getTo().getY(),
                route.getTo().getZ(),
                route.getTo().getName(),
                route.getKey()
        );
        return dto;
    }

    @Override
    public String getName() {
        return "get_routes";
    }

    @Override
    public String getDescription() {
        return "возвращает маршруты";
    }
}
