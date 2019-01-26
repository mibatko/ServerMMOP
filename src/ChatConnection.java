import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ChatConnection {

    private Socket socket;
    private String username;
    private int userNumber;

    private BufferedReader input;
    private PrintWriter output;

    private static int numberOfConnections = 0;

    private ChatConnection(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(),true);

            numberOfConnections++;
            this.userNumber = numberOfConnections;
        }
        catch(IOException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }

    ChatConnection(Socket socket) {
        this(socket, socket.getInetAddress().getHostAddress());
    }

    BufferedReader getInputBufferedReader() {
        return this.input;
    }

    PrintWriter getOutputPrintWriter() {
        return this.output;
    }

    String getUsername() {
        return this.username;
    }

    int getUserNumber() {
        return userNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    int getNumberOfConnections() {
        return numberOfConnections;
    }

    void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }
}
