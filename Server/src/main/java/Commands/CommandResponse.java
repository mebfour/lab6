package Commands;

public class CommandResponse {
    private final String message;
    private final boolean success;

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



    @Override
    public String toString() {
        return "CommandResponse{" +
                "message='" + this.getMessage() + '\'' +
                ", success=" + this.isSuccess() +
                '}';
    }
}
