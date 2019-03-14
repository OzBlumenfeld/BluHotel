import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Service {
    private ServerSocket server = null;
    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter  output = null;


    public Service(int port){
        try {
            server = new ServerSocket(port);
            System.out.println("Server initiated");
            clientSocket = server.accept();
            System.out.println("Client accepted by server");
            output = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String roomAvailable() {
        return "room 10 is available";
    }

    private void serve(HotelDatabase hdb,Boolean html) throws Exception{
        String line ;
        System.out.println("Serving");
        /**Todo: implement a protocol
         * 1 = Print customers database.
         * 2 = Add new customer
         * 3 = Delete customer.
         * 4 = Add room.
         * 5 = Show hotel prices
         * and so on...
         */
        if (!html) {
            String response;
            while ((line = in.readLine()) != null) {
                System.out.println("Echo: " + line);
                response = myProtocol(line, hdb);
                output.println(response);
            }
            System.out.println("Closing connection");
            clientSocket.close();
            in.close();
        }
        else
            httpHandler(hdb);
    }
    private String httpHandler(HotelDatabase hdb)throws Exception{
        String line;
        line = in.readLine();
        StringBuilder raw = new StringBuilder();
        raw.append("" + line);
        boolean isPost = line.startsWith("POST");
        int contentLength = 0;
        while (!(line = in.readLine()).equals("")) {
            raw.append('\n' + line);
            if (isPost) {
                final String contentHeader = "Content-Length: ";
                if (line.startsWith(contentHeader)) {
                    contentLength = Integer.parseInt(line.substring(contentHeader.length()));
                }
            }
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
        Vector<String> vector = getValues(bodyStr,bodyStr.split("&").length);
        boolean status = hdb.addCustomer(vector);
        // send response
        sendResponse(output,isPost,body.toString(),status);


        output.flush();
        output.close();
        clientSocket.close();
        return "";
    }

    private void sendResponse(PrintWriter output,boolean isPost,String body,boolean status) {

        output.println("HTTP/1.1 200 OK\r\n");
        output.println("Content-Type: text/html\r\n");
        output.println("\r\n");
        output.println(new Date().toString());
        if (isPost && status) {
            output.println("<br><u> Reservation success! :" + body + "</u>");
        }
        else if (isPost && !status ) {
            output.println("<br><u> Reservation failure! :" + body.toString() + "</u>");
        }else {
            output.println("<form method='POST'>");
            output.println("<input name='name' type='text'/>");
            output.println("<input type='submit'/>");
            output.println("</form>");
        }
    }

    private Vector<String> getValues(String body,int valuesCount){
        Vector<String> vector = new Vector<>();
        for (int i = 0; i < valuesCount;i++){
            String[] comp = body.split("=");
            vector.add(comp[i+1].split("&")[0]);
        }
        return vector;
    }
    private String HTTPProtocol(String[] httpRequest,String input){
        String response = "Handling HTTP request";
        if (input.contains("HTTP"))
            httpRequest[0] =  getRequest(input);
        if (input.contains("Content-Type:"))
            response = "finished";
        return response;
    }
    private boolean isHTTPProtocol(String line){
        return (line.contains("HTTP") || line.contains("Host:") || line.contains("Connection")
                ||line.contains("Content-Length:"));
    }
    private String getRequest(String input) {
        String response = "";
        String[] components = input.split("/");
        String request = components[0];
        if(request.equals("POST")){
            System.out.println("Got post request");
            response = response;
        }

        return response;
    }

    private String myProtocol(String input,HotelDatabase hdb) throws Exception{
        String name;
        int id;
        String room;
        String[] components = input.split(",");
        int request = Integer.parseInt(components[0]);
        switch (request){
            /**print customers database request
             *
             * **/
            case 1:
                String result;
                System.out.println("inside switch case 1");
                HashMap<Integer, Vector<String>> customers = hdb.getCustomersRecords();
                HashMap<Integer, Vector<String>> rooms = hdb.getRoomsRecords();
                if (components[1].equals("rooms"))
                    result = roomsRecords(rooms);
                else if (components[1].equals("customers"))
                        result = customersRecords(customers);
                else result = roomsRecords(rooms) + customersRecords(customers);
                return result;

            /**Add customer request
             * **/
            case 2:
                System.out.println("Not working command need to fix");
                name = components[1];
                id = Integer.parseInt(components[2]);
                room = components[3];
//                hdb.addCustomer(name,id,room);
                return "Add customer request completed successfully";

            /** Delete customer request**/
            case 3:
                String field = components[1];
                String value = components[2];
                hdb.deleteRecord(field,value);
                return "Deleted record successfully";
            case 4:
                int roomId = Integer.parseInt(components[1]);
                int size = Integer.parseInt(components[2]);
                String busy = components[3];
                hdb.addRoom(roomId,size,busy);
                return "Added room successfully";
            /** Reset rooms**/
            case 5:
                hdb.resetRooms();


                return"Reset success";
        }
        return "Error didn't select any of the available options";
    }


    public static void main(String[] args) throws Exception {
        Service service = new Service(5000);
        HotelDatabase hdb = new HotelDatabase();
//        hdb.CreateCustomersTable();
//        System.out.println("DB create complete");
        if(args[0].equals("html"))
            service.serve(hdb,true);
        else
            service.serve(hdb,false);
    }
    private String customersRecords (HashMap<Integer,Vector<String>> customers){
        int id;
        String name;
        String room;
        String result = "Customers records: ";
        for (Map.Entry<Integer,Vector<String>> entry: customers.entrySet()) {
            id = entry.getKey();
            name = entry.getValue().get(0);
            room = entry.getValue().get(1);
            result +=  " $ name: " + name + " id: " + id + " room #: " + room ;
        }
        return result;
    }
    private String roomsRecords (HashMap<Integer,Vector<String>> rooms){
        int id;
        String size;
        String busy;
        String result = "Rooms:";
        for (Map.Entry<Integer,Vector<String>> entry: rooms.entrySet()) {
            id = entry.getKey();
            size = entry.getValue().get(0);
            busy = entry.getValue().get(1);
            result +=  " $ room #:" + id + " size: " + size + " busy: " + busy ;
        }
        return result;
    }


}
