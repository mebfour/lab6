package Commands;

@FunctionalInterface
public interface IdChecker {
    boolean checkIdOnServer(int id);
}
