import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Główna klasa aplikacji
public class Server {

    private int port;
    private List<ChatConnection> connections;

    private static final int DEFAULT_PORT = 60321;

    public static void main(String[] args) {
        //Utworzenie głównej instancji klasy Server z użyciem wybranego portu
        Server chatServer = new Server(DEFAULT_PORT);
        //Uruchomienie serwera
        chatServer.startServer();
    }

    //Konstruktor
    private Server(int port) {
        //Ustawienie portu
        this.port = port;
        //Utworzenie tablicy, zawierającej wszytskie nawiązane połączenia z klientami
        //TODO: Check if <> is better then using explicit type
        this.connections = new ArrayList<ChatConnection>();
    }

    private void startServer() {
        //Kod zawarty w bloku try-catch by złapać możliwy wyjątek IOException - wymagane z uwagi na używane klasy/metody
        try {
            //Utworzenie gniazda sieciowego serwera na odpowiednim porcie
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("Started MMOP Server using port " + this.port);

            //Nieskończona pętla obsługująca nowe nadchodzące połączenia
            //TODO: Check if there's a better way of handling infinite loop
            while(true) {
                //Zapisanie referencji na temat nadchodzącego połączenia
                Socket incomingConnection = serverSocket.accept();

                System.out.println("New connection established with: " + incomingConnection.getInetAddress().getHostAddress());

                //Utworzenie nowej instancji klasy ChatConnection reprezentującej nowo ustanowione połączenie
                ChatConnection newChatConnection = new ChatConnection(incomingConnection);
                //Dodanie nowego połączenia to tablicy istniejącyhc połączeń
                //TODO: Add option for connection that sends username in first message.
                this.connections.add(newChatConnection);

                //Wysłanie wiadomości powitalnej do klienta który właśnie się połączył
                //TODO: Add info for other users that someone joined.
                newChatConnection.getOutputPrintWriter().println("Welcome to MMOP Server! User count: " + newChatConnection.getNumberOfConnections());

                //Utworzenie nowej instancji klasy Netcode, która pozwala wielowątkowo obsługiwać nowe połączenie
                //W tym wypadku jest to klasa dziedzicząca po klasie Thread, dlatego po utowrzeniu wywołujemy metodę start()
                Netcode netcode = new Netcode(this, newChatConnection);
                netcode.start();
            }
        }
        //Obsługa wyjątku poprzez wydrukowanie treści błędu do konsoli
        catch(IOException error) {
            System.out.println("Server error: " + error.getMessage());
        }
    }

    //Głowne działąnie serwera - pozwól klientom na zlecenie rozesłania wiadomości do wszytskich
    //Metoda rozsyłająca wiadomość do wszytskich aktualnie połączonych klientów.
    void sendMessage(String message, ChatConnection sender) {
        //Pętla for-each powtarzająca ten sam kod dla każdego aktualnie podłączonego klienta
        for(ChatConnection connection : this.connections) {
            String fullMessage;

            //TODO: Print local time, not server time.
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            fullMessage = dateFormat.format(date) + " " + sender.getUsername() + ": " + message;

            //Przeslanie wiadomosci po wczeniejsym dodaniu do niej aktualnej godziny i username'a klienta ktory nadał wiadomość.
            connection.getOutputPrintWriter().println(fullMessage);
        }
    }

    //Metoda zamykająca instniejące połączenie.
    void closeConnection(ChatConnection chatConnection) {
        //TODO: Add info for other users that someone left.
        System.out.println("Connection closed with user number: " + chatConnection.getUserNumber());
        //Usunięcie połączenia z tablicy aktualnych połączeń
        this.connections.remove(chatConnection);
    }

}
