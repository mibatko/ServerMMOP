import java.io.BufferedReader;
import java.io.IOException;

//Klasa obsługująca czynności mające miejsce podczas połączenia.
//Ta klasa wprowadza wielowątkowość, dlatego dziedziczy po klasie Thread
public class Netcode extends Thread {

    //Jako łącznik pomiędzy serwerem a klientem, musi posiadać referencje do obu
    private Server server;
    private ChatConnection chatConnection;

    //Konstruktor
    Netcode(Server server, ChatConnection chatConnection) {
        this.server = server;
        this.chatConnection = chatConnection;
    }

    //Gdy dziedziczymy po klasie Thread, kod do wykonania wielowątkowo musi znaleźć się w metodzie run()
    //Z kloei wywolany musi zostać poprzez metodę start() ktora jest juz w klasie Thread
    @Override
    public void run() {
        String message;
        BufferedReader input = chatConnection.getInputBufferedReader();

        //Główne czynność obsługująca klienta
        //W nieskończonej pętli czekaj na nadchodzącą wiadomośc a następnie zleć serwerowi rozesłąnie tej wiadomości do wszystkich
        try {
            while(true) {
                //Odczytaj wiadomość
                message = input.readLine();
                //TODO: Need better disconnection/closing handle.
                if(message.equals("exit"))
                    break;
                else
                    //Roześlij do wsztskich połączonych klientów poprzez serwer
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
