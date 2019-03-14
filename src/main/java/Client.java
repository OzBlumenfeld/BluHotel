import javafx.scene.chart.PieChart;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter  output = null;
    private Scanner scanner = null;

    public Client(String address,int port){
        try {
            socket = new Socket(address,port);
            System.out.println("Connected into server");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(),true);
//            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            BufferedReader stdIn = new BufferedReader(
                            new InputStreamReader(System.in));
            System.out.println("Connected into server");
//            in = new DataInputStream(System.in);
//            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            String line = "";
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                System.out.println("userInput = " + userInput);
                output.println(userInput);
                System.out.println("Sent message to Server");
                line = in.readLine();
                System.out.println("Recieved: \n" + line);
            }

            System.out.println("Client out");
            in.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1",5000);
    }
}
