import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

public class HotelDatabase {
    private Connection conn = null;
    private Statement statement = null;
    private final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost/hotel?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    //  Database credentials
    private final String USER = "root";
    private final String PASS = "oz223322";

    public HotelDatabase(){
        try {
            connectToDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectToDB() throws Exception{
        Class.forName(DRIVER).newInstance();
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        System.out.println("Welcome to Blu Hostel :)");
    }
    public boolean addCustomer(Vector<String> vect) throws Exception{
        this.connectToDB();
        statement = conn.createStatement();
        int numGuests = Integer.parseInt(vect.get(6));
        int room = checkAvailableRoom(numGuests);
        if (room == -1)
            return false;
        String query = " insert into customers (id, name, email, credit, arrival, depart, guests, room)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?)";
        int personId = Integer.parseInt(vect.get(1));
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1,personId); //id
        preparedStatement.setString(2,vect.get(0)); //name
        preparedStatement.setString(3,vect.get(2)); //email
        preparedStatement.setString(4,vect.get(3)); //credit
        preparedStatement.setString(5,vect.get(4)); //arrival date
        preparedStatement.setString(6,vect.get(5)); //departure date
        preparedStatement.setInt(7,numGuests); //guests number
        preparedStatement.setInt(8,room); //room number
        preparedStatement.execute();
        query = " UPDATE rooms SET busy = "+ personId+ " WHERE id = "+room;
        statement.executeUpdate(query);
        updateRoomStatus(statement,personId,room);
        conn.close();
        return true;
    }
    public void updateRoomStatus(Statement statement,int personId,int room)throws Exception{
        String query = " UPDATE rooms SET busy = "+ personId+ " WHERE id = "+room;
        statement.executeUpdate(query);
    }

    private int checkAvailableRoom(int numGuests) throws Exception{
        int id;
        connectToDB();
        statement =  conn.createStatement();
        String query = "Select id FROM rooms WHERE busy=0 AND size=" + numGuests;
        statement.executeQuery(query);
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
             return rs.getInt("id");
        }
        return -1;
    }

    private void createTable(String name){//needed?

        String query = "CREATE TABLE "+ name;
        try {
            statement.executeUpdate(query);
            System.out.println("Database created successfully...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                    System.out.println("statement closed");
                    conn.close();
                    System.out.println("connection closed");
                }
            }
                catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }
    public HashMap<Integer,Vector<String>> getCustomersRecords() throws Exception{
        this.connectToDB();
        statement = conn.createStatement();
        String query = "SELECT * FROM customers";
        ResultSet rs = statement.executeQuery(query);
        HashMap<Integer,Vector<String>> customers = new HashMap<Integer, Vector<String>>();
        Vector<String> identifications ;
        while (rs.next()){
            String name = rs.getString("name");
            int id = rs.getInt("id");
            String room = rs.getString("room");

            identifications = new Vector<String>();
            identifications.add(name);
            identifications.add(room);

            customers.put(id,identifications);
        }
        conn.close();
        return customers;
    }
    public HashMap<Integer,Vector<String>> getRoomsRecords() throws Exception{
        this.connectToDB();
        statement = conn.createStatement();
        String query = "SELECT * FROM rooms";
        ResultSet rs = statement.executeQuery(query);
        HashMap<Integer,Vector<String>> customers = new HashMap<Integer, Vector<String>>();
        Vector<String> identifications ;
        while (rs.next()){
            int roomId = rs.getInt("id");
            int size = rs.getInt("size");
            String busy = rs.getString("busy");

            identifications = new Vector<String>();
            identifications.add(size+"");
            identifications.add(busy);

            customers.put(roomId,identifications);
        }
        conn.close();
        return customers;
    }


    public void deleteRecord(String field,String value) throws Exception{
        this.connectToDB();
        statement = conn.createStatement();
        String query = "DELETE FROM customers WHERE "+ field + "=" + value;
        statement.executeUpdate(query);
        conn.close();
    }

    public static void main(String[] args) {
        HotelDatabase hdb = new HotelDatabase();
        try {
//            hdb.addCustomer("oz",308458314,"1");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Service server = new Service();

//        hdb.addCustomer();
    }

    public boolean addUserInfo(Vector<String> vect) throws Exception{
        //Input: int id,String userName,String password,String email
        this.connectToDB();
        statement = conn.createStatement();

//       Todo: check if user unique if not return false
        String query = " insert into users (id, user_name, password, email)"
                + " values (?, ?, ?, ?)";
        int personId = Integer.parseInt(vect.get(2));
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(2,vect.get(0)); //userName
        preparedStatement.setString(3,vect.get(1)); //password
        preparedStatement.setInt(1,personId); //id
        preparedStatement.setString(4,vect.get(3)); //email
        preparedStatement.execute();
        conn.close();
        return true;
    }

    public void CreateCustomersTable() throws Exception{
        System.out.println("in create customers");
        connectToDB();
        statement = conn.createStatement();

        String query = "CREATE TABLE customers ( "+
                "id INT(9) UNSIGNED PRIMARY KEY," +
                "name VARCHAR(30) NOT NULL," +
                "email VARCHAR(40) NOT NULL," +
                "credit VARCHAR(30) NOT NULL," +
                "arrival VARCHAR(30) NOT NULL," +
                "depart VARCHAR(30) NOT NULL," +
                "guests INT(1) NOT NULL," +
                "room INT(3) NOT NULL" +
                ")";
        statement.executeUpdate(query);
    }

    public void addRoom(int roomId, int size, String busy) throws Exception {
        connectToDB();
        statement = conn.createStatement();
        String query = " insert into rooms (id, size, busy)"
                + " values (?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1,roomId);
        preparedStatement.setInt(2,size);
        preparedStatement.setString(3,busy);
        preparedStatement.execute();
        conn.close();
    }

    public void resetRooms() throws Exception {
        connectToDB();
        Statement statement = conn.createStatement();
        for (int i = 1; i < getNumOfRooms() + 1;i++){
            updateRoomStatus(statement,0,i);
        }
    }

    private int getNumOfRooms() throws Exception {
        int i = 0;
        connectToDB();
        Statement statement = conn.createStatement();
        String query = "Select * FROM rooms";
        statement.executeQuery(query);
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            i++;
        }
        return i;
    }

    public String login(Vector<String> vector) throws Exception {
        String username = vector.get(0);
        String password = vector.get(1);
        int id = 0;

        connectToDB();
        statement =  conn.createStatement();
        String query = "SELECT id FROM users WHERE user_name= '"+username+"' AND password= "+ password;
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            id = rs.getInt("id");
        }
        if (id == 0)
            return "Failed login";
        statement =  conn.createStatement();
        query = "SELECT customers.name, customers.room, customers.arrival, customers.depart " +
                "FROM customers INNER JOIN users ON customers.id= "+ id;

        rs = statement.executeQuery(query);
        String userInfo = "You didn't reserve any room yet";
        while (rs.next()) {
            String name = rs.getString("name");
            String room = rs.getString("room");
            String arrival = rs.getString("arrival");
            String depart = rs.getString("depart");
            userInfo = "Name: "+ name+ ", Room number: " +room +", arrival date: " +arrival
                    + ", departure date: " +depart;
            break;
        }

        return userInfo;
    }
}



