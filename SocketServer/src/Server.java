import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    ServerSocket mServer;
    int serverPort = 9090;
    ArrayList<Thread> threads = new ArrayList<Thread>();
    int limit = 3;

    //HashMap<String,ClientManager> clientsMap=new HashMap<String, ClientManager>();
    public Server() {
        try {
            // create server socket!
            mServer = new ServerSocket(serverPort);
            System.out.println("Server Created!");
            // always running
            while (true) {
                Socket client = mServer.accept();
                System.out.println("Connected to New Client!");

                Thread t = new Thread(new ClientManager(this, client));
                // add Thread to "threads" list
                threads.add(t);
                if (threads.size() >= limit) {
                    t.start();
                } else {
                    System.out.println("the are not enough users! please wait");
                }

            }
        } catch (IOException e) {
        }
    }

    //public void addClientManager(String clientName,ClientManager cm){
    //  clientsMap.put(clientName, cm);
    //}

    public static void main(String[] args) {
        new Server();
    }
}
