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
        Label commandsLabel = new Label(Localization.getString("able_comms"));
        ListView<String> commandsView = new ListView<>();
        commandsView.getItems().addAll(
                Localization.getString("add_route"),
                Localization.getString("remove_route_by_key") ,
                Localization.getString("edit_route") ,
                Localization.getString("execute_script")
        );
        commandsView.setPrefHeight(100);

        // информация о коллекции
        Label collectionInfoLabel = new Label("Информация о коллекции:");
        Label typeLabel = new Label( Localization.getString("type") +": Map<String, RouteDTO>");

        VBox collectionBox = new VBox(5,
                typeLabel,
                sizeLabel,
                dateLabel
        );

        // Добавляем всё во вкладку
        content.getChildren().addAll(commandsLabel, commandsView, collectionInfoLabel, collectionBox);
        // Подписка на изменение локали
        Localization.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateLocalizedText(commandsLabel, commandsView, collectionInfoLabel, typeLabel);
        });
        // Подписка на изменение routeMap
        routeMapProperty.addListener((obs, oldMap, newMap) -> updateInfo(newMap));
        if (routeMapProperty.get() != null) {
            updateInfo(routeMapProperty.get());
        }
    }
    private void updateLocalizedText(Label commandsLabel, ListView<String> commandsView,
                                     Label collectionInfoLabel, Label typeLabel) {
        // Обновляем текст элементов
        commandsLabel.setText(Localization.getString("able_comms"));
        commandsView.getItems().setAll(
                Localization.getString("add_route"),
                Localization.getString("remove_route_by_key"),
                Localization.getString("edit_route"),
                Localization.getString("execute_script")
        );
        collectionInfoLabel.setText(Localization.getString("main_info:"));
        typeLabel.setText(Localization.getString("type") + ": Map<String, RouteDTO>");
    }

    private void updateInfo(Map<String, RouteDTO> routeMap) {
        // Обновление размера
        sizeLabel.setText(Localization.getString("num_elts")+ ": " + routeMap.size());

        // Обновление даты первой записи
        if (!routeMap.isEmpty()) {
            Optional<RouteDTO> firstRoute = routeMap.values().stream()
                    .min(Comparator.comparingLong(RouteDTO::getCreationDate));
            dateLabel.setText(firstRoute.map(route -> Localization.getString("date_of_firts_route")+ ": " + new Date(route.getCreationDate()))
                    .orElse( Localization.getString("date_of_firts_route")+ ": —"));
        } else {
            dateLabel.setText(Localization.getString("date_of_firts_route")+ ": —");
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
