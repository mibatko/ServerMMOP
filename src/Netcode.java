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

                if(message.startsWith("/username ")) {
                    server.sendSystemMessageToAll("User " + chatConnection.getUsername() + " changed his username to: " + message.substring(10));
                    chatConnection.setUsername(message.substring(10));
                }
                else if(message.startsWith("/exit")) {
                    break;
                }
                else {
                    server.sendChatMessage(message, chatConnection);
                }
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
