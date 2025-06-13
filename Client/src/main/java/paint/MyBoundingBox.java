package paint;

import Classes.RouteDTO;

public class MyBoundingBox {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final RouteDTO route;

    // Конструктор, который ты используешь
    public MyBoundingBox(double x, double y, double width, double height, RouteDTO route) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.route = route;
    }

    // Проверка, попал ли клик внутрь области
    public boolean contains(double px, double py) {
        return px >= x - width / 2 && px <= x + width / 2 &&
                py >= y - height / 2 && py <= y + height / 2;
    }

    // Геттер для получения связанного маршрута
    public RouteDTO getRoute() {
        return route;
    }
}
