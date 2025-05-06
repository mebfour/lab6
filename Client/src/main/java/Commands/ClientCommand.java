package Commands;

import java.io.IOException;

public interface ClientCommand {
    void clientExecute(String[] args) throws IOException;
}

