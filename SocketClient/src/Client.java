import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
public class Client {
    Socket mSocket;
    int port = 9090;
    String serverAddress = "127.0.0.1";
    public Client() {
        try {
            mSocket = new Socket(serverAddress, port);
            System.out.println("connect to server ....");
        }
        catch (UnknownHostException e) { }
        catch (IOException e) {
            System.out.println(e.getMessage()); }
    }
    public static void main(String[] args) {
        new Client();
    }
}
