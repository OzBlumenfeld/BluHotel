import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RunnableServer implements Runnable {
    private ServerSocket server ;
    private Socket clientSocket ;
    private BufferedReader in ;
    private PrintWriter output;
    private HotelDatabase hdb ;
    private boolean htmlProtocol ;

    RunnableServer(ServerSocket serverInput,Socket socket,BufferedReader inInput
            ,PrintWriter out,HotelDatabase hotelDB,boolean html){
        super();
        server = serverInput;
        clientSocket = socket;
        in = inInput;
        output = out;
        hdb = hotelDB;
        htmlProtocol = html;
    }

    @Override
    public void run() {
        try {
            serve(hdb,htmlProtocol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void serve(HotelDatabase hdb,Boolean html) throws Exception{
        String line ;
        System.out.println("Serving");

        if (!html) {
            String response;
            MyProtocol protocol = new MyProtocol();
            while ((line = in.readLine()) != null) {
                System.out.println("Echo: " + line);
                response = protocol.runProtocol(line, hdb);
                output.println(response);
            }
            System.out.println("Closing connection");
            clientSocket.close();
            in.close();
        }
        else{
            LimitedHttpProtocol protocol = new LimitedHttpProtocol(in,output,clientSocket);
            protocol.httpRequestHandler(hdb);
        }
    }
}