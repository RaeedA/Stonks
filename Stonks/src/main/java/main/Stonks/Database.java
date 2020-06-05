package main.Stonks;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

public class Database
{
    MySQLAccess access;
    
    public Database()
    {
        access = new MySQLAccess();
    }
    
    public JSONObject buy(String symbol, int quantity, String username) throws SQLException
    {
        ResultSet set;
        JSONObject obj = new JSONObject();
        
        //Get ID
        set = access.execute( "select id, balance from userinfo where username = '" + username + "'" );
        if (set == null)
        {
            obj.put( "success", false );
            obj.put( "message", "user doesn't exist" );
            return obj;
        }
        set.next();
        int id = set.getInt( 1 );
        
        //temp empty price
        int price = 10;
        
        //check if they have money
        if(set.getDouble( 2 ) < price*quantity)
        {
            obj.put( "success", false );
            obj.put( "message", "not enough money" );
            return obj;
        }
        
        java.sql.Statement st = access.getStatement();
        
        //Check if stock exists and make it if it doesn't
        set = access.execute( "select id from stockinfo where symbol = '" + symbol + "' and userid = " + id );
        if (!set.next())
        {
            st.executeUpdate("insert into stockinfo (userid, symbol, volume, realized, costbasis) values (" + id + ", '" + symbol + "', 0, 0, 0)" );
        }
        
        //Audit log update
        st.executeUpdate( "insert into transactions(stockid, transactiontime, transactiontype, unitprice, transferamount) values ((select id from stockinfo where userid = " + id + " and symbol = '" + symbol + "'), now(), 'buy' , " + price + ", " + quantity + ")" );
        
        //Change balance
        st.executeUpdate( "update userinfo set balance=balance-(" + quantity + "*" + price + ")  where id = " + id );
        
        //Change stock amount
        st.executeUpdate( "update stockinfo set volume = volume + " + quantity + ", costbasis = ( costbasis * (volume-" + quantity + ") + (" + price + " * " + quantity + ")) /(volume) where symbol = '" + symbol + "' and userid = " + id );
        
        obj.put( "success", true );
        obj.put( "message", "task complete");
        return obj;
    }
    
    public JSONObject sell(String symbol, int quantity, String username) throws SQLException
    {
        ResultSet set;
        JSONObject obj = new JSONObject();
        
        //Get ID
        set = access.execute( "select id from userinfo where username = '" + username + "'" );
        if (set == null)
        {
            obj.put( "success", false );
            obj.put( "message", "user doesn't exist" );
            return obj;
        }
        set.next();
        int id = set.getInt( 1 );
        
        //temp empty price
        int price = 10;
        
        //make sure they have enough to sell
        set = access.execute( "select volume from stockinfo where symbol = '" + symbol + "' and userid = " + id );
        if (!set.next())
        {
            obj.put( "success", false );
            obj.put( "message", "stock doesnt exist" );
            return obj;
        }
        if (set.getInt( 1 ) < quantity)
        {
            obj.put( "success", false );
            obj.put( "message", "not enough stock to sell" );
            return obj;
        }
        
        java.sql.Statement st = access.getStatement();
        
        //Audit log update
        st.executeUpdate( "insert into transactions(stockid, transactiontime, transactiontype, unitprice, transferamount) values ((select id from stockinfo where userid = " + id + " and symbol = '" + symbol + "'), now(), 'sell' , " + price + ", " + quantity + ")" );
        
        //Update user balance
        st.executeUpdate("update userinfo set balance=balance + (" + quantity + "*" + price + ") where id = " + id);
        
        //Update stock amounts
        st.executeUpdate( "update stockinfo set volume = volume - " + quantity + ", realized = " + price + "*" + quantity + " where symbol = '" + symbol + "' and userid = " + id );
        
        obj.put( "success", true );
        obj.put( "message", "task complete");
        return obj;
    }
    
    public JSONObject getStockPrice()
    {
        /* 
         * StockStatus (In: [Stock symbol, quantity], Out: [Stock symbol, price])
         * JSON: In: { "info" : [ "SYMBOL" , 10 ] }, Out: { "info" : [ “SYMBOL" , 0.01 ] }
         */
        return null;
        
    }
    
    public ResultSet run(String query)
    {
        ResultSet set = access.execute( query );
        return set;
    }
    
    public void close()
    {
        access.close();
    }
}
