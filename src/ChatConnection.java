import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//Klasa reprezentująca połączenie z klientem. Funkcjonuje głownie jako przechowalnia informacji o połączeniu.
class ChatConnection {

    //Informacje o połączonym kliencie
    private Socket socket;
    private String username;
    private int userNumber;
    //Strumienie wejścia i wyjścia aktualnego połączenia służace do wysyłąnia i odbierania wiadomości
    private BufferedReader input;
    private PrintWriter output;

    //Statyczny atrybut liczący ilość instancji tej klasy
    //TODO: Make sure the number goes down when someone disconnects.
    private static int numberOfConnections = 0;

    //Konstruktor
    private ChatConnection(Socket socket, String username) {
        //Ponownie try-catch gdyż tego wymaga używanie strumieni.
        try {
            this.socket = socket;
            this.username = username;
            //Strumien wejscia jest buforowany. Usprawnia przesyl danych. Sugerowane rozwiązanie na podstawie wykładów.
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Przesył na strumien wyjscia tez upsrawniony wg sgestii z wykłądów. Autoflush to flaga zapobiegająca potrzebie pisania flush co chwilę.
            this.output = new PrintWriter(socket.getOutputStream(),true);
            //Dodanie 1 do liczby instniejących instancji, gdyż właśnie tworzymy kolejną
            numberOfConnections++;
            this.userNumber = numberOfConnections;
        }
        catch(IOException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }

    //Alternatywny konstruktor, gdy nie podany został username
    ChatConnection(Socket socket) {
        this(socket, "Anonymous");
    }

    //Klika getterów, żeby inne klasy miały dostęp do atrybutów tej klasy. W tym wypadku package protected.
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

    int getNumberOfConnections() {
        return numberOfConnections;
    }

    //Metoda zamykająca gniazdo sieciowe przy zakończeniu połączenia
    void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }
}
