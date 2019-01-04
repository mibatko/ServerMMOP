import java.io.BufferedReader;
import java.io.IOException;

public class Netcode extends Thread {

    private Server server;
    private ChatConnection chatConnection;

    Netcode(Server server, ChatConnection chatConnection) {
        this.server = server;
        this.chatConnection = chatConnection;
    }

    @Override
    public void run() {
        String message;

        BufferedReader input = chatConnection.getInputBufferedReader();

        try {
            while(true) {
                message = input.readLine();
                //TODO: Need better disconnection/closing handle.
                if(message.equals("exit"))
                    break;
                else
                    server.sendMessage(message, chatConnection);
            }
        }
        catch(IOException error) {
            System.out.println("Server error: " + error.getMessage());
        }
        finally {
            server.closeConnection(chatConnection);
            chatConnection.closeConnection();
        }
    }
}
