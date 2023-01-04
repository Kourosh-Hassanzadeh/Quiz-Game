import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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


            while (true) {
                JSONParser parser = new JSONParser();
                try{
                    Object obj = parser.parse(new FileReader("../questions.json"));
                    questionHandler(obj);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void questionHandler(Object obj){
        JSONArray array = (JSONArray)obj;
        for(int i = 0 ; i<array.size(); i++){
            JSONObject object = (JSONObject)array.get(i);
            String question = (String)object.get("question");
            JSONArray options = (JSONArray)object.get("options");
            Iterator iterator = options.iterator();
            long answer = (long) object.get("answer");
            writer.println(question);
            while(iterator.hasNext()) {
                writer.println(iterator.next());
            }
            Scanner sc = new Scanner(System.in);
            int n = sc.nextInt();
            if(n == answer){
                System.out.println("correct");
            }
        }

    }

}