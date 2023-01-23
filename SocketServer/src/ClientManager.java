import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientManager extends Thread {
    Socket clientHolder;
    Server serverHolder;
    String clientName;
    InputStream fromClientStream;
    OutputStream toClientStream;
    DataInputStream reader;
    PrintWriter writer;
    int score = 0;

    public ClientManager(Server server, Socket client, String name) {
        serverHolder = server;
        clientHolder = client;
        clientName = name;
    }

    @Override
    public void run() {
        try {
            fromClientStream = clientHolder.getInputStream();
            toClientStream = clientHolder.getOutputStream();

            reader = new DataInputStream(fromClientStream);
            writer = new PrintWriter(toClientStream, true);

            serverHolder.addClientManager(clientName, this);

            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(new FileReader("../questions.json"));
                questionHandler(obj);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void questionHandler(Object obj) throws InterruptedException {

        JSONArray array = (JSONArray) obj;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            String question = (String) object.get("question");
            writer.println(question);
            JSONArray options = (JSONArray) object.get("options");
            for (Object option : options) {
                writer.println(option);
            }
            serverHolder.allow2chat = false;
            Thread.sleep(45000);
            serverHolder.allow2chat = true;
            int answer = (int) (long) object.get("answer");
            Scanner s = new Scanner(fromClientStream);
            String ans = s.nextLine();
            if (ans.equals("")) {
                writer.println("No answer has entered");
            }
            //else if (ans.length() == 1) {                                // یعنی احتمالا جواب است

            else if (answer == Integer.parseInt(ans)) {
                score++;
                writer.println("Correct");
                serverHolder.updateScore();
            } else {
                writer.println("Wrong Answer");
            }
            //  }
            writer.println(serverHolder.names + " score is: " + Server.scores);
            //Thread.sleep(5000);
            Thread.sleep(25000);
        }
    }

    public int getScore() {
        return score;
    }
}