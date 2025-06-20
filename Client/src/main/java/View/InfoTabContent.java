package View;

import Classes.RouteDTO;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class InfoTabContent {

    private final VBox content = new VBox(15);
    private final Label sizeLabel = new Label();
    private final Label dateLabel = new Label();

    public InfoTabContent(ObjectProperty<Map<String, RouteDTO>> routeMapProperty) {
        //  Стили
        content.setStyle("-fx-padding: 10; -fx-font-size: 14px;");

        // список команд
        Label commandsLabel = new Label("Доступные команды:");
        ListView<String> commandsView = new ListView<>();
        commandsView.getItems().addAll(
                "Добавить маршрут",
                "Удалить маршрут по ключу",
                "Редактировать маршрут",
                "Выполнить скрипт"
        );
        commandsView.setPrefHeight(100);

        // информация о коллекции
        Label collectionInfoLabel = new Label("Информация о коллекции:");
        Label typeLabel = new Label("Тип: Map<String, RouteDTO>");

        VBox collectionBox = new VBox(5,
                typeLabel,
                sizeLabel,
                dateLabel
        );

        // Добавляем всё во вкладку
        content.getChildren().addAll(commandsLabel, commandsView, collectionInfoLabel, collectionBox);

        // Подписка на изменение routeMap
        routeMapProperty.addListener((obs, oldMap, newMap) -> updateInfo(newMap));
        if (routeMapProperty.get() != null) {
            updateInfo(routeMapProperty.get());
        }
    }

    private void updateInfo(Map<String, RouteDTO> routeMap) {
        // Обновление размера
        sizeLabel.setText("Количество элементов: " + routeMap.size());

        // Обновление даты первой записи
        if (!routeMap.isEmpty()) {
            Optional<RouteDTO> firstRoute = routeMap.values().stream()
                    .min(Comparator.comparingLong(RouteDTO::getCreationDate));
            dateLabel.setText(firstRoute.map(route -> "Дата первой записи: " + new Date(route.getCreationDate()))
                    .orElse("Дата первой записи: —"));
        } else {
            dateLabel.setText("Дата первой записи: —");
        }
    }

    public Tab getTab() {
        Tab infoTab = new Tab(Localization.getString("information"));
        infoTab.setClosable(false);
        BorderPane tabContent = new BorderPane();
        tabContent.setCenter(content);
        infoTab.setContent(tabContent);
        return infoTab;
    }
}
