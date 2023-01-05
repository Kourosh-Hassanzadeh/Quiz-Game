import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientManager extends Thread {
    Socket clientHolder;
    Server serverHolder;
    InputStream fromClientStream;
    OutputStream toClientStream;
    DataInputStream reader;
    PrintWriter writer;

    public ClientManager(Server server, Socket client) {
        serverHolder = server;
        clientHolder = client;
    }

    @Override
    public void run() {
        try {
            // input stream (stream from client)
            fromClientStream = clientHolder.getInputStream();
            // output stream (stream to client)
            toClientStream = clientHolder.getOutputStream();

            reader = new DataInputStream(fromClientStream);
            writer = new PrintWriter(toClientStream, true);


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

    public void questionHandler(Object obj) {

        int score = 0;
        JSONArray array = (JSONArray) obj;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            String question = (String) object.get("question");
            writer.println(question);
            JSONArray options = (JSONArray) object.get("options");
            for (Object option : options) {
                writer.println(option);
            }

            int answer = (int) (long) object.get("answer");
            Scanner s = new Scanner(fromClientStream);
            String ans = s.nextLine();
            if (answer == Integer.parseInt(ans)) {
                writer.println("correct");
                score++;
            } else {
                writer.println("wrong answer");
            }
            writer.println(score);
        }
    }
}