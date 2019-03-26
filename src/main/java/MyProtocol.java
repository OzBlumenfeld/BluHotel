import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MyProtocol {
    public String runProtocol(String input,HotelDatabase hdb) throws Exception{
/**Todo: implement a protocol
 * 1 = Print customers database.
 * 2 = Add new customer
 * 3 = Delete customer.
 * 4 = Add room.
 * 5 = Show hotel prices
 * and so on...
 */
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
//                name = components[1];
//                id = Integer.parseInt(components[2]);
//                room = components[3];
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


}
