import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {
    Socket mSocket;
    int port = 9090;
    String serverAddress = "127.0.0.1";
    InputStream fromServerStream;
    OutputStream toServerStream;
    DataInputStream reader;
    PrintWriter writer;

    public Client() {
        try {

            mSocket = new Socket(serverAddress, 1025);
            System.out.println("connect to server ....");

            fromServerStream = mSocket.getInputStream();
            toServerStream = mSocket.getOutputStream();

            reader = new DataInputStream(fromServerStream);
            writer = new PrintWriter(toServerStream, true);

            Scanner s = new Scanner(fromServerStream);
            questionHandler(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void questionHandler(Scanner s) {

        while(true) {

            String question = s.nextLine();
            System.out.println(question);

            for (int i = 0; i < 4 ; i++) {
                String option = s.nextLine();
                System.out.println(option);
            }

            Scanner sc = new Scanner(System.in);
            String ans = sc.nextLine();

            writer.println(ans);

            System.out.println(s.nextLine());
            System.out.println(s.nextLine());
        }
    }

    public static void main(String[] args){
        new Client();
    }
}

