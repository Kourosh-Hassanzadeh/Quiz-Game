import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {
    Socket mSocket;
    //int port = 9090;
    String serverAddress = "127.0.0.1";
    InputStream fromServerStream;
    OutputStream toServerStream;
    DataInputStream reader;
    PrintWriter writer;
    Socket chatSocket;
    InputStream chatInputStream;
    OutputStream chatOutputStream;
    DataInputStream chatReceive;
    PrintWriter chatSend;
    Scanner sc;


    public Client() {
        this.sc = new Scanner(System.in);
        try {


            mSocket = new Socket(serverAddress, 1032);
            chatSocket = new Socket(serverAddress, 1033);
            System.out.println("connect to server ....");

            chatInputStream = chatSocket.getInputStream();
            chatOutputStream = chatSocket.getOutputStream();

            fromServerStream = mSocket.getInputStream();
            toServerStream = mSocket.getOutputStream();

            chatReceive = new DataInputStream(chatInputStream);
            chatSend = new PrintWriter(chatOutputStream, true);

            reader = new DataInputStream(fromServerStream);
            writer = new PrintWriter(toServerStream, true);

//            Thread clientChatSend = new Thread(new ClientChatAnswerSend(this));
//            clientChatSend.start();
            Thread clientChatReceive = new Thread(new ClientChatReceive(this));  // receiving msg and put it on terminal
            clientChatReceive.start();

            Scanner s = new Scanner(fromServerStream);
            Thread questionHandler = new Thread(new QuestionHandler(this, s, sc));
            questionHandler.start();

            questionChatHandler();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void questionChatHandler() {
        String ans;

        while (true) {
            ans = sc.nextLine();
            if (ans.contains("#")) {                 // chat
                chatSend.println(ans);
            } else
                writer.println(ans);
        }
    }

    private static class QuestionHandler extends Thread {

        Client client;
        Scanner s;
        Scanner sc;

        QuestionHandler(Client client, Scanner s, Scanner sc) {
            this.client = client;
            this.s = s;
            this.sc = sc;
        }

        @Override
        public void run() {
            while (true) {

                String question = s.nextLine();
                System.out.println(question);

                for (int i = 0; i < 4; i++) {
                    String option = s.nextLine();
                    System.out.println(option);
                }

                System.out.println(s.nextLine());
                System.out.println(s.nextLine());

            }
        }

    }

//    private static class ClientChatAnswerSend extends Thread {
//        private Client client;
//
//        ClientChatAnswerSend(Client client) {
//            this.client = client;
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//                synchronized (client.sc) {
//                    String ans = client.sc.nextLine();
//                    System.out.println("hi");
//                    if (ans.contains("#")) {
//                        client.chatSend.println(ans);
////                        System.out.println(ans);
//                    }
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }
//
//    }

    private static class ClientChatReceive extends Thread {

        private Client client;
        private Scanner chatScanner;

        ClientChatReceive(Client client) {
            this.client = client;
            this.chatScanner = new Scanner(client.chatInputStream);
        }

        @Override
        public void run() {
            while (true) {
//              System.out.println("here");
                String message = chatScanner.nextLine();
                String[] messageDivide = message.split("#");
                System.out.println(messageDivide[0] + " says " + messageDivide[1]);
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}

