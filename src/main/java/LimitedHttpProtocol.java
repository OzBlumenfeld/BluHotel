import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

public class LimitedHttpProtocol {
    private BufferedReader in = null;
    private PrintWriter output = null;
    private Socket clientSocket = null;


    public LimitedHttpProtocol(BufferedReader input,PrintWriter out,Socket sock){
        in = input;
        output = out;
        clientSocket = sock;
    }

    public String httpRequestHandler(HotelDatabase hdb)throws Exception{
        String line;
        line = in.readLine();
        StringBuilder raw = new StringBuilder();
        System.out.println(line);
        raw.append("" + line);
        boolean isPost = line.startsWith("POST");
        boolean isGet = line.startsWith("GET");
        int contentLength = 0;
        while (!(line = in.readLine()).equals("")) {
            System.out.println(line);
            raw.append('\n' + line);
            if (isPost) {
                final String contentHeader = "Content-Length: ";
                if (line.startsWith(contentHeader)) {
                    contentLength = Integer.parseInt(line.substring(contentHeader.length()));
                }
            }
        }
        if (isGet){
            System.out.println(raw.toString());
            return "Get request";
        }
        StringBuilder body = new StringBuilder();
        if (isPost) {
            int c = 0;
            for (int i = 0; i < contentLength; i++) {
                c = in.read();
                body.append((char) c);
            }
        }
        raw.append(body.toString());

        String bodyStr = body.toString();
        System.out.println(bodyStr);

        Vector<String> vector = getValues(bodyStr,bodyStr.split("&").length);
//        //Todo: Implement decode(body) function that recognizes the request from client
        int requestIdentifier = decodeClientRequest(bodyStr);

        // Todo: encode function
        boolean status = false;
        if(requestIdentifier == 1)
            status = hdb.addUserInfo(vector);
        else if (requestIdentifier == 2)
            status = hdb.addCustomer(vector);
        else if (requestIdentifier == 3){
             bodyStr = hdb.login(vector);
             if (!bodyStr.equals("You didn't reserve any room yet") && !bodyStr.equals("Failed login"))
                 status = true;
        }
        // send response
        sendResponse(output,isPost,bodyStr,status,requestIdentifier);

        output.flush();
        output.close();
//        clientSocket.close();
        return "";
    }
    private Vector<String> getValues(String body,int valuesCount){
        Vector<String> vector = new Vector<>();
        for (int i = 0; i < valuesCount;i++){
            String[] comp = body.split("=");
            vector.add(comp[i+1].split("&")[0]);
        }
        return vector;
    }

    private void sendResponse(PrintWriter output,boolean isPost,String body,boolean status,int request) {

        output.println("HTTP/1.1 200 OK\r\n");
//        output.println("Content-Type: text/html\r\n");
        output.println("\r\n");
        output.println(new Date().toString());
        if (isPost && status) {
            if (request == 1)
                output.println(" Sign up success! :" + body + "");
            if (request == 2)
                output.println("Reservation success! :" + body + "</u>");
            if (request == 3){
                output.println("Login success!\nReservations :\n" + body + "</u>");

            }
        }
        else if (isPost && !status ) {
            if (request == 2)
                output.println(" Reservation failure! :" + body );
            if (request == 3)
                output.println(body );

        }else {
            output.println("<form method='POST'>");
            output.println("<input name='name' type='text'/>");
            output.println("<input type='submit'/>");
            output.println("</form>");
        }
    }
    public int decodeClientRequest(String body){
        if (body.startsWith("SignUpName")) {
            return 1;
        }
        if (body.startsWith("reservationUserName")){
            return 2;
        }
        if (body.startsWith("LoginName")) {
            return 3;
        }
        return 0;
    }

}
