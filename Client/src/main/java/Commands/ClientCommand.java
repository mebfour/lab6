package Commands;

import InputHandler.InputProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public interface ClientCommand {
    void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException, NoSuchAlgorithmException;
    String getName();
}

