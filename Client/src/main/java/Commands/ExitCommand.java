package Commands;

import InputHandler.InputProvider;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ExitCommand implements ClientCommand {
    private final SocketChannel socketChannel;

    public ExitCommand(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {
        System.out.println("Завершение работы клиента...");
        socketChannel.close();
        System.exit(0);
    }

    @Override
    public String getName() {
        return "exit";
    }
}
