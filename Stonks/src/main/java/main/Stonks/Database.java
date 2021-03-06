package main.Stonks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class Database
{
    MySQLAccess access;
    StockFinder finder;
    
    public Database(StockFinder stock)
    {
        access = new MySQLAccess();
        finder = stock;
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
        HashMap<String, Double> prices = finder.find( symbol );
        double price = prices.get( symbol );
        
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
        HashMap<String, Double> prices = finder.find( symbol );
        double price = prices.get( symbol );
        
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
    
    public JSONObject accstatus(String username) throws SQLException
    {
        /*Out: { "stockinfo" : [ "SYMBOL" , QUANTITY , COSTBASIS , REALIZEDGAIN , UNREALIZEDGAIN , PRICE ] , "balance" : BALANCE, "message" : MESSAGE , "success", SUCCESS}*/
        ResultSet set;
        JSONObject obj = new JSONObject();
        
        //Get ID and balance
        set = access.execute( "select id, balance from userinfo where username = '" + username + "'" );
        if (set == null)
        {
            obj.put("success", false);
            obj.put( "message", "user doesn't exist" );
            return obj;
        }
        set.next();
        int id = set.getInt( 1 );
        double balance = set.getDouble( 2 );
        
        
        JSONArray arr = new JSONArray();
        
        set = access.execute( "select symbol, volume, costbasis, realized from stockinfo where userid = " + id );
        if (set != null)
        {
            while(set.next())
            {
                JSONObject o = new JSONObject();
                o.put( "symbol", set.getString( 1 ) );
                o.put( "volume", set.getInt( 2 ) );
                o.put( "costbasis", set.getDouble( 3 ) );
                o.put( "realizedgain", set.getDouble( 4 ) );
                arr.add( o );
            }
        }
        
        String[] symbols = new String[arr.size()];
        for(int i = 0; i < arr.size(); i++)
        {
            JSONObject o = (JSONObject)arr.get( 0 );
            symbols[i] = (String)o.get( "symbol" );
        }
        
        HashMap<String, Double> map = finder.find( symbols );
        
        for(int i = 0; i < arr.size(); i++)
        {
            JSONObject o = (JSONObject)arr.get( 0 );
            o.put( "price", (Double)map.get( (String)o.get( "symbol" ) ) );
        }
        
        obj.put( "stockinfo", arr );
        obj.put( "balance", balance );
        obj.put( "success", true );
        obj.put( "message", "task complete");
            
        return obj;
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
