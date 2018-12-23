import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        System.out.println("Started MMOP Server.");

        try(ServerSocket serverSocket = new ServerSocket(6045)) {
            while (true) {
                Socket socket = serverSocket.accept();
                Netcode netcode = new Netcode(socket);
                netcode.start();
            }
        }
        catch (IOException exception) {
            System.out.println("System error: " + exception.getMessage());
        }

        System.out.println("Closing MMOP Server.");
    }
}
