package View;

import javafx.beans.property.SimpleObjectProperty;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Localization {
    private static final Map<Locale, Map<String, String>> localizedStrings = new HashMap<>();
    private static Locale currentLocale = Locale.getDefault();
    private static ZoneId currentZone;
    private static final SimpleObjectProperty<Locale> localeProperty =
            new SimpleObjectProperty<>(Locale.getDefault());

    static {
        // Русский
        Map<String, String> ru = new HashMap<>();
        ru.put("add", "Добавить");
        ru.put("remove", "Удалить");
        ru.put("edit", "Редактировать");
        ru.put("user_label", "Пользователь: ");
        ru.put("title_route_info", "Информация о маршруте");
        ru.put("id", "ID");
        ru.put("name", "Название");
        ru.put("x", "X");
        ru.put("y", "Y");
        ru.put("owner", "Владелец");
        ru.put("creation_date", "Дата создания");
        ru.put("from", "From");
        ru.put("to", "To");
        ru.put("key", "Ключ");
        ru.put("from_name", "From Name");
        ru.put("to_name", "To Name");
        ru.put("routes", "Маршруты");
        ru.put("information", "Информация");
        ru.put("script", "Скрипт");
        ru.put("execute_script", "Выполнить скрипт");
        ru.put("fill in all fields", "Заполните все поля");
        ru.put("tip_path", "Путь к файлу");
        ru.put("add_new_route","Добавление нового маршрута");
        ru.put("x_coord_l", "Координата X (long)");
        ru.put("y_coord_i","Координата Y (int)");
        ru.put("name_to","Название пункта назначения");
        ru.put("x_coord_f","Координата X (float)");
        ru.put("z_coord_i","Координата Z (int)");
        ru.put("name_from","Название пункта отправления");
        ru.put("main_info:","Основная информация:");
        ru.put("route_name:", "Название маршрута:");
        ru.put("coords:", "Координаты:");
        ru.put("to_place:", "Пункт назначения:");
        ru.put("from_place: ","Пункт отправления:" );
        ru.put("inp_err", "Ошибка ввода");
        ru.put("check_correct", "Проверьте правильность введенных числовых значений");


        localizedStrings.put(new Locale("ru"), ru);

        // Нидерландский
        Map<String, String> nl = new HashMap<>();
        nl.put("add", "Toevoegen");
        nl.put("remove", "Verwijderen");
        nl.put("edit", "Bewerken");
        nl.put("user_label", "Gebruiker: ");
        nl.put("title_route_info", "Route-informatie");
        nl.put("id", "ID");
        nl.put("name", "Naam");
        nl.put("x", "X");
        nl.put("y", "Y");
        nl.put("owner", "Eigenaar");
        nl.put("creation_date", "Aanmaakdatum");
        nl.put("from", "Van");
        nl.put("to", "Naar");
        nl.put("key", "Sleutel");
        nl.put("from_name", "Van Naam");
        nl.put("to_name", "Naar Naam");
        nl.put("routes", "Routekaart");
        nl.put("information", "Informatie");
        nl.put("script", "Script");
        nl.put("execute_script", "Script uitvoeren");
        nl.put("fill in all fields", "Vul alle velden in");
        nl.put("tip_path", "Vul alle velden in");

        localizedStrings.put(new Locale("nl"), nl);

        // Датский
        Map<String, String> da = new HashMap<>();
        da.put("add", "Tilføj");
        da.put("remove", "Slet");
        da.put("edit", "Rediger");
        da.put("user_label", "Bruger: ");
        da.put("title_route_info", "Ruteinformation");
        da.put("id", "ID");
        da.put("name", "Navn");
        da.put("x", "X");
        da.put("y", "Y");
        da.put("owner", "Ejer");
        da.put("creation_date", "Oprettelsesdato");
        da.put("from", "Fra");
        da.put("to", "Til");
        da.put("key", "Nøgle");
        da.put("from_name", "Fra Navn");
        da.put("to_name", "Til Navn");
        da.put("routes", "Ruter");
        da.put("information", "Information");
        da.put("script", "Skript");
        da.put("execute_script", "Kør script");
        da.put("fill in all fields", "Udfyld alle felter");
        da.put("tip_path", "Udfyld alle felter");

        localizedStrings.put(new Locale("da"), da);

        // Английский (Индия)
        Map<String, String> en_IN = new HashMap<>();
        en_IN.put("add", "Add");
        en_IN.put("remove", "Remove");
        en_IN.put("edit", "Edit");
        en_IN.put("user_label", "User: ");
        en_IN.put("title_route_info", "Route Information");
        en_IN.put("id", "ID");
        en_IN.put("name", "Name");
        en_IN.put("x", "X");
        en_IN.put("y", "Y");
        en_IN.put("owner", "Owner");
        en_IN.put("creation_date", "Creation Date");
        en_IN.put("from", "From");
        en_IN.put("to", "To");
        en_IN.put("key", "Key");
        en_IN.put("from_name", "From Name");
        en_IN.put("to_name", "To Name");
        en_IN.put("routes", "Routes");
        en_IN.put("information", "Information");
        en_IN.put("script", "Script");
        en_IN.put("execute_script", "Execute Script");
        en_IN.put("fill in all fields", "Fill in all fields");
        en_IN.put("tip_path", "The file path");

        localizedStrings.put(new Locale("en", "IN"), en_IN);
    }

    public static ZoneId getCurrentZone() {
        return currentZone;
    }

    public static void setCurrentZone(ZoneId currentZone) {
        Localization.currentZone = currentZone;
    }

    public static void setLocale(Locale locale) {
        System.out.println("Изменили локацию на " + locale);
        currentLocale = locale;
        localeProperty.set(locale);
        currentZone = getZoneIdByLocale(locale);
        if (localizedStrings.containsKey(locale)) {
            currentLocale = locale;
        }
    }



    public static ZoneId getZoneIdByLocale(Locale locale) {
        if (locale == null) {
            return ZoneId.systemDefault(); // или ZoneId.of("UTC")
        }

        return switch (locale.getLanguage()) {
            case "da" -> ZoneId.of("Europe/Copenhagen");  // Дания
            case "nl" -> ZoneId.of("Europe/Amsterdam");   // Нидерланды
            case "en" -> ZoneId.of("Asia/Kolkata");
            default -> ZoneId.of("Europe/Moscow");         // Системный пояс
        };
    }


    public static String getString(String key) {
        return localizedStrings.get(currentLocale).getOrDefault(key, "???" + key + "???");
    }

    public static NumberFormat getNumberFormat() {
        return NumberFormat.getInstance(currentLocale);
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", currentLocale);
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(localizedStrings.keySet());
    }


    public static SimpleObjectProperty<Locale> localeProperty() {
        return localeProperty;
    }

    public static String formatDate(long timestamp, ZoneId targetZone) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime userTime = instant.atZone(targetZone);

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss", currentLocale);
        return userTime.format(formatter);
    }
}