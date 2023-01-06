import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    ServerSocket mServer;
    int serverPort=1025;
    int limit = 3;
    ArrayList<Thread> threads = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    static ArrayList<Integer> scores = new ArrayList<>();
    HashMap<String, ClientManager> map = new HashMap<>();

    public Server() throws IOException {
        int index = 0;
        try {
            mServer = new ServerSocket(serverPort);
            System.out.println("Server Created!");

            JSONParser parser1 = new JSONParser();
            try {
                Object object = parser1.parse(new FileReader("../users.json"));
                userHandler(object);
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (true) {
                Socket client = mServer.accept();
                System.out.println("Connected to New Client!");

                Thread t = new Thread(new ClientManager(this, client, names.get(index)));
                threads.add(t);
                t.setName(names.get(index));
                scores.add(0);
                index++;

                if (threads.size() == limit) {

                    for(int j=0 ; j<threads.size() ; j++) {
                        threads.get(j).start();
                    }
                }
                else if(threads.size()>3){
                    t.start();
                }
                else {
                    System.out.println("the are not enough users! please wait");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mServer.close();
    }

    public void userHandler(Object object) {
        JSONArray accounts = (JSONArray) object;
        for (int i = 0; i < accounts.size(); i++) {
            JSONObject obj = (JSONObject) accounts.get(i);
            String type = (String) obj.get("type");
            long port = (Long) obj.get("port");
            String name = (String) obj.get("name");
            if(type.equals("client")){
                names.add(name);
            }
            if(type.equals("host")){
                serverPort= (int) port;
            }
        }
    }
    public ClientManager findClient(String name){
        return map.get(name);
    }
    public void addClientManager(String clientName,ClientManager cm){
        map.put(clientName, cm);
    }

    public void updateScore(){
        for(int i =0 ; i< names.size() ; i++){
            ClientManager cm = findClient(names.get(i));
            scores.set(i, cm.getScore());
        }
        System.out.println(names + " score is: " + scores);
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
