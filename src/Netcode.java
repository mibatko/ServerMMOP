import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Netcode extends Thread {

    private Socket socket;

    Netcode(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Client connected (Thread#" + this.getId() + ")");
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);

            while (true) {
                String echoString = input.readLine();
                System.out.println("Thread#" + this.getId() + ": " + echoString);
                if (echoString.equals("exit"))
                    break;
                echoString = printDate() + " Thread#" + this.getId() + ": " + echoString;
                output.println(echoString);
            }
        }
        catch (IOException exception) {
            System.out.println("Server error: " + exception.getMessage());
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException exception) {
                System.out.println("Server error: " + exception.getMessage());
            }
        }
    }

    private String printDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
