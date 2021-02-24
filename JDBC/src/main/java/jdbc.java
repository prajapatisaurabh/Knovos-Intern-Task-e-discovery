import java.sql.*;

public class jdbc {
    public static void main(String[] args) {
        Connection conn = null;


try{
            String dbURL = "jdbc:mysql://127.0.0.1:3306/movies";
            String user = "";
            String password = "";

            Class.forName("com.mysql.cj.jdbc.Driver");
           conn = DriverManager.getConnection(dbURL, user, password);

    System.out.println("Suceess...");
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
//        finally {
//            try{
//                if(conn!=null && !conn.isClosed()){
//                    conn.close();
//                }
//            }
//            catch (SQLException e){
//                e.printStackTrace();
//            }
//        }
    }
}
