package Commands.Modif;

import Commands.Command;
import Commands.CommandResponse;

import static managers.CommandManager.collectionManager;

public class RemoveByKey implements Command {
    @Override
    public CommandResponse execute(String args) {

        String key = (args != null) ? args.trim() : "";

        if (key.isEmpty()) {
            return new CommandResponse("Ключ не передан.", false);
        }

        if (collectionManager.getCollection().isEmpty()) {
            return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
        }

        if (collectionManager.getCollection().containsKey(key)) {
            collectionManager.removeConcrFromBD(key);
            collectionManager.getCollection().remove(key);
            collectionManager.saveToFile(); // сохраняем изменения
            return new CommandResponse("Элемент с ключом " + key + " успешно удалён.", true);
        } else {
            return new CommandResponse("Элемент с ключом " + key + " не найден.", false);
        }
    }



    @Override
    public String getName() {
        return "remove_by_key";
    }

    @Override
    public String getDescription() {
        return "удаляет элемент из коллекции по его ключу";
    }
}
