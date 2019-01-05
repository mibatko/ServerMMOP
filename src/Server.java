import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Server {

    private int port;
    private List<ChatConnection> connections;

    public static void main(String[] args) {
        Server chatServer = new Server(60321);
        chatServer.startServer();
    }

    private Server(int port) {
        this.port = port;
        //TODO: Check if <> is better then using explicit type
        this.connections = new ArrayList<ChatConnection>();
    }

    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("Started MMOP Server using port " + this.port);

            //TODO: Check if there's a better way of handling infinite loop
            while(true) {
                Socket incomingConnection = serverSocket.accept();

                System.out.println("New connection established with: " + incomingConnection.getInetAddress().getHostAddress());

                ChatConnection newChatConnection = new ChatConnection(incomingConnection);
                //TODO: Add option for connection that sends username in first message.
                this.connections.add(newChatConnection);

                newChatConnection.getOutputPrintWriter().println("Welcome to MMOP Server! User count: " + newChatConnection.getNumberOfConnections());

                Netcode netcode = new Netcode(this, newChatConnection);
                netcode.start();
            }
        }
        catch(IOException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }

    void sendMessage(String message, ChatConnection sender) {
        for(ChatConnection connection : this.connections) {
            String fullMessage;

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            fullMessage = dateFormat.format(date) + " " + sender.getUsername() + ": " + message;

            connection.getOutputPrintWriter().println(fullMessage);
        }
    }

    void closeConnection(ChatConnection chatConnection) {
        System.out.println("Connection closed with user number: " + chatConnection.getUserNumber());
        this.connections.remove(chatConnection);
    }

}
