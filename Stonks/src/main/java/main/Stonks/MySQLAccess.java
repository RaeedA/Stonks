package main.Stonks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLAccess {
    private Connection connect = null;
    private Statement statement = null;
    //private PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    public MySQLAccess()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost/stonks?&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&user=root&password=H4ppy:)!");
            statement = connect.createStatement();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
    }
    
    public ResultSet execute(String query)
    {
        try
        {
            return statement.executeQuery(query);
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public void readDataBase() throws Exception {
        try {
            // This will load the MySQL driver, each DB has its own driver
            
            // Setup the connection with the DB

            // Statements allow to issue SQL queries to the database
            
            // Result set get the result of the SQL query
            
            /*// PreparedStatements can use variables and are more efficient
            preparedStatement = connect.prepareStatement("insert into  feedback.comments values (default, ?, ?, ?, ? , ?, ?)");
            
            // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement.setString(1, "Test");
            preparedStatement.setString(2, "TestEmail");
            preparedStatement.setString(3, "TestWebpage");
            preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
            preparedStatement.setString(5, "TestSummary");
            preparedStatement.setString(6, "TestComment");
            preparedStatement.executeUpdate();

            preparedStatement = connect
                    .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);

            // Remove again the insert comment
            preparedStatement = connect
            .prepareStatement("delete from feedback.comments where myuser= ? ; ");
            preparedStatement.setString(1, "Test");
            preparedStatement.executeUpdate();

            resultSet = statement
            .executeQuery("select * from feedback.comments");
            writeMetaData(resultSet);*/

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

    /*private void writeMetaData(ResultSet resultSet) throws SQLException {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
    }*/

    public Statement getStatement()
    {
        try
        {
            return connect.createStatement();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String saveResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            int id = resultSet.getInt("id");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            double balance = resultSet.getDouble("balance");
            return "Id: " + id + " Username: " + username + " Password: " + password + " Balance: " + balance;
        }
        return "";
    }
    
    public String getUserInfo()
    {
        try
        {
            return saveResultSet(resultSet);
        }
        catch ( SQLException e )
        {
            return e.toString();
        }
    }

    // You need to close the resultSet
    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

}