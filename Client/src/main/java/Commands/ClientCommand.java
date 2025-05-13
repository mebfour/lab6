package Commands;

import InputHandler.InputProvider;

import java.io.IOException;
import java.util.Scanner;

public interface ClientCommand {
    void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException;
    String getName();
}

