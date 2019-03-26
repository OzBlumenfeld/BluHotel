
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Service {
    private ServerSocket server = null; //Singleton
    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter  output = null;

    public Service(int port){
        try {
            if (server == null){
                server = new ServerSocket(port);
                System.out.println("Server initiated");
            }

            clientSocket = server.accept();
            System.out.println("Client accepted by server");
            output = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        /*Todo: Implement more scalable server.
        * Todo: Implement the protocol correctly.
        * */
        final int MAX_THREADS = 10;
        ExecutorService threadpool = Executors.newFixedThreadPool(MAX_THREADS);
        HotelDatabase hdb = new HotelDatabase();
        boolean htmlProtocol = false;
        Service s ;
        ServerSocket server = new ServerSocket(5000);
//        hdb.CreateCustomersTable();
//        System.out.println("DB create complete");
        if(args[0].equals("html")) {
            htmlProtocol = true;
        }
        Socket clientSocket;
        while (true){
            clientSocket = server.accept();
            System.out.println("Client accepted by server");
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            RunnableServer run = new RunnableServer(server,clientSocket
                                ,in,output,hdb,htmlProtocol);
            threadpool.submit(run);
        }
    }

}
