package Commands;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ClientCommand {
    void clientExecute(String[] args, String pars) throws IOException, NoSuchAlgorithmException;
    String getName();
}

