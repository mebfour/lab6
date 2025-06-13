package ToStart;

import Classes.RouteDTO;

import java.util.Map;

public class CommandResponse {
    private final String message;
    private final boolean success;
    private Map<String, RouteDTO> routeList;

    public CommandResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<String, RouteDTO> getRouteList() {return routeList;}


    @Override
    public String toString() {
        return "CommandResponse{" +
                "message='" + this.getMessage() + '\'' +
                ", success=" + this.isSuccess() +
                '}';
    }
}
