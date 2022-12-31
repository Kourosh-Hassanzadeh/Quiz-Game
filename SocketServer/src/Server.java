import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    ServerSocket mServer;
    int serverPort = 9090;

    public Server() {
        try {
            // create server socket!
            mServer = new ServerSocket(serverPort);
            System.out.println("Server Created!");
            // wait for client
            mServer.accept(); // horaaaaa
            System.out.println("Connected to New Client!");
        }
        catch (IOException e) {}
    }

    public static void main(String[] args) {
        new Server();
    }
}


