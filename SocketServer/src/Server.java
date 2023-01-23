import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Server {
    ServerSocket mServer;
    ServerSocket chatSocket;
    int serverPort = 1032;
    int chatSocketPort = 1033;
    ChatHandler chatHandler;
    int limit = 3;
    boolean allow2chat = false;
    int usersCount = 0;
    ArrayList<Thread> threads = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    static ArrayList<Integer> scores = new ArrayList<>();
    HashMap<String, ClientManager> map = new HashMap<>();
    HashMap<Integer, Message> chatsMap = new HashMap<>(); // int -> which user


    public Server() throws IOException {
        int index = 0;
        try {
            mServer = new ServerSocket(serverPort);
            chatSocket = new ServerSocket(chatSocketPort);
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
                Socket chat = chatSocket.accept();
                System.out.println("Connected to New Client!");

                Thread t = new Thread(new ClientManager(this, client, names.get(index)));
                //System.out.println(names.get(index));
                threads.add(t);
                t.setName(names.get(index));
                scores.add(0);

                // new
                //adding new chat to chatsMap
//                DataInputStream chatDataInputStream = new DataInputStream(chat.getInputStream());
                PrintWriter chatPrintWriter = new PrintWriter(chat.getOutputStream(), true);
                int key = Integer.parseInt(names.get(index).split("-")[1]);
                chatsMap.put(key, new Message(chat.getInputStream(), chatPrintWriter));

                chatHandler = new ChatHandler(this, names.get(index));
                Thread chatHandlerThread = new Thread(chatHandler);
                chatHandlerThread.start();

                usersCount++;
                index++;

//                if (threads.size() == limit) {
                if (usersCount == limit) {
                    for (int j = 0; j < threads.size(); j++) {
                        threads.get(j).start();
                    }
                }
//                else if(threads.size()>3){
                else if (usersCount > 3) {
                    t.start();
                } else {
                    System.out.println("there are not enough users! please wait");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mServer.close();
    }

    private static class Message {  //new function
        DataInputStream dataInputStream;
        InputStream inputStream;
        Scanner scanner;
        PrintWriter printWriter;

        Message(InputStream inputStream, PrintWriter printWriter) {
            this.inputStream = inputStream;
            this.dataInputStream = new DataInputStream(inputStream);
            this.scanner = new Scanner(inputStream);
            this.printWriter = printWriter;
        }

    }

    private static class ChatHandler extends Thread {

        private Server server;
        private String username;
        private int key;
        private HashMap<Integer, Message> map;

        ChatHandler(Server server, String username) throws IOException {
            this.server = server;
            this.username = username;
            this.key = Integer.parseInt(username.split("-")[1]);
            this.map = server.chatsMap;
        }

        @Override
        public void run() {
            while (true) {
//                System.out.println("test");
                while (true) {
//                    System.out.println(this.username + "CHAT HANDLER");
                    String message = map.get(this.key).scanner.nextLine();
                    if (message.contains("#")) {
                        System.out.println(message);
                        String[] dataMessage = message.split("#");
                        String sender;
                        if (server.allow2chat) {
                            sender = this.username;
                            System.out.println(this.username + " says to " + dataMessage[0] + ": " + dataMessage[1]);
                            server.chatsMap.get(Integer.parseInt(dataMessage[0])).printWriter.println(sender + "#" + dataMessage[1]);
                        } else {
                            System.out.println(this.username + " wanted to say to " + dataMessage[0] + ": " + dataMessage[1]);
                            server.chatsMap.get(this.key).printWriter.println("Host#Sorry, time to play!");
                        }
                    }

                }
            }
        }

    }

    public void userHandler(Object object) {
        JSONArray accounts = (JSONArray) object;
        for (int i = 0; i < accounts.size(); i++) {
            JSONObject obj = (JSONObject) accounts.get(i);
            String type = (String) obj.get("type");
            long port = (Long) obj.get("port");
            String name = (String) obj.get("name");
            if (type.equals("client")) {
                names.add(name);
            }
            if (type.equals("host")) {
                serverPort = (int) port;
            }
        }
    }

    public ClientManager findClient(String name) {
        return map.get(name);
    }

    public void addClientManager(String clientName, ClientManager cm) {
        map.put(clientName, cm);
    }

    public void updateScore() {
        for (int i = 0; i < names.size(); i++) {
            ClientManager cm = findClient(names.get(i));
            scores.set(i, cm.getScore());
        }
        System.out.println(names + " score is: " + scores);
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
