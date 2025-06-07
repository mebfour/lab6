package Commands.Modif;

import Commands.Command;
import Commands.CommandResponse;

import static Collection.RouteCollectionManager.routeList;
import static managers.CommandManager.collectionManager;
import static users.LoginCommand.username;

public class RemoveByKey implements Command {


    @Override
    public CommandResponse execute(String args) {
        try {
            String key = (args != null) ? args.trim() : "";

            if (key.isEmpty()) {
                return new CommandResponse("Ключ не передан.", false);
            }

            if (collectionManager.getCollection().isEmpty()) {
                return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
            }

            if (collectionManager.getCollection().containsKey(key)) {

                if (routeList.get(key).getOwner().equals(username)) {
                    collectionManager.removeConcrFromBD(key);
                    collectionManager.getCollection().remove(key);
                    return new CommandResponse("Элемент с ключом " + key + " успешно удалён.", true);
                } else {
                    return new CommandResponse("Ошибка доступа: объект Вам не принадлежит", false);
                }


            } else {
                return new CommandResponse("Элемент с ключом " + key + " не найден.", false);
            }
        }catch (Exception e) {
            System.out.println("Ошибка удаления элемента");
        }
        return null;
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
