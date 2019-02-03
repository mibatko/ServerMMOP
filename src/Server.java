import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.*;

public class Server {

    private int port;
    private List<ChatConnection> connections;
    private Connection dbConnection;

    private static final int DEFAULT_PORT = 60321;

    public static void main(String[] args) {
        Server chatServer = new Server(DEFAULT_PORT);
        chatServer.startServer();
    }

    private Server(int port) {
        this.port = port;
        this.connections = new ArrayList<ChatConnection>();
    }

    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("Started MMOP Server using port " + this.port);

            connectToDB();

            while(true) {
                Socket incomingConnection = serverSocket.accept();

                System.out.println("New connection established with: " + incomingConnection.getInetAddress().getHostAddress());

                ChatConnection newChatConnection = new ChatConnection(incomingConnection);
                this.connections.add(newChatConnection);

                sendSystemMessageToAll(newChatConnection.getUsername() + " joined MMOP server. Welcome! Current user count: " + newChatConnection.getNumberOfConnections());

                if(connections.size() == 1) {
                    switchDrawingClient();
                }

                Netcode netcode = new Netcode(this, newChatConnection);
                netcode.start();
            }
        }
        catch(IOException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }

    void sendChatMessage(String message, ChatConnection sender) {
        for(ChatConnection connection : this.connections) {
            String fullMessage;

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            fullMessage = dateFormat.format(date) + " " + sender.getUsername() + ": " + message;

            connection.getOutputPrintWriter().println(fullMessage);
        }
        archiveMessage(sender.getUsername(), message);
    }

    void sendSystemMessage(String message, ChatConnection receiver) {
        String fullMessage = "\tSERVER INFO: " + message;
        receiver.getOutputPrintWriter().println(fullMessage);
    }

    void switchDrawingClient() {
        ChatConnection temp = connections.get(0);
        connections.remove(0);
        connections.add(temp);
        sendSystemMessage("isDrawing=true", connections.get(0));
    }

    void sendSystemMessageToAll(String message) {
        for(ChatConnection connection : this.connections) {
            sendSystemMessage(message, connection);
        }
    }

    void closeConnection(ChatConnection chatConnection) {
        System.out.println("Connection closed with user number: " + chatConnection.getUserNumber());
        this.connections.remove(chatConnection);
    }

    void connectToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "mmop", "mmop2019");

            Statement statement = dbConnection.createStatement();
            String sqlString = "CREATE DATABASE IF NOT EXISTS MMOP;";
            statement.execute(sqlString);
            sqlString = "USE MMOP;";
            statement.execute(sqlString);
            sqlString = "CREATE TABLE IF NOT EXISTS Chat_History (MessageID int NOT NULL AUTO_INCREMENT, Username varchar(40), Message varchar(1023), PRIMARY KEY (MessageID));";
            statement.execute(sqlString);


        } catch (ClassNotFoundException | SQLException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }

    void archiveMessage(String sender, String message) {
        try {
            Statement statement = dbConnection.createStatement();
            String sqlString = "INSERT INTO Chat_History (Username, Message) VALUES ('" + sender + "', '" + message + "');";
            statement.execute(sqlString);
        } catch (SQLException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }

}
