package Commands;

import java.io.IOException;

public interface ClientCommand<T> {
    T clientExecute(String[] args) throws IOException;
}

