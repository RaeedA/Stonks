package main.Stonks;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.json.simple.JSONObject;

@SpringBootApplication
@RestController
public class StonksApplication
{    
    public static void main(String[] args)
    {
        SpringApplication.run(StonksApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name)
    {
        return String.format("Hello %s!", name);
    }
    
    @GetMapping("/test")
    public String test()
    {
        Database d = new Database();
        ResultSet set = d.run( "select * from userinfo where id = 1" );
        try
        {
            System.out.println(set.next());
            return set.getString( "username" );
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            return "failed";
        }
        finally
        {
            d.close();
        }
    }
    
    @GetMapping("/buy")
    public JSONObject buy(@RequestParam(value = "symbol", defaultValue = "") String symbol, @RequestParam(value = "amount", defaultValue = "") String amount, @RequestParam(value = "username", defaultValue = "") String username)
    {
        JSONObject obj =  new JSONObject();
        
        if (symbol.equals( "" ) || amount.equals( "" ) || username.equals( "" ))
        {
            obj.put( "success", false );
            obj.put( "message", "not enough information");
            return obj;
        }
        int quantity;
        
        try
        {
            quantity = Integer.parseInt( amount );
        }
        catch(NumberFormatException e)
        {
            obj.put("success", false);
            obj.put("message", "not a valid number");
            return obj;
        }
        Database d = new Database();
        try
        {
            obj = d.buy( symbol, quantity, username );
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        d.close();
        return obj;
    }
    
    @GetMapping("/sell")
    public JSONObject sell(@RequestParam(value = "symbol", defaultValue = "") String symbol, @RequestParam(value = "amount", defaultValue = "") String amount, @RequestParam(value = "username", defaultValue = "") String username)
    {
        JSONObject obj =  new JSONObject();  
        
        if (symbol.equals( "" ) || amount.equals( "" ) || username.equals( "" ))
        {
            obj.put( "success", false );
            obj.put( "message", "not enough information");
            return obj;
        }
        int quantity;
        
        try
        {
            quantity = Integer.parseInt( amount );
        }
        catch(NumberFormatException e)
        {
            obj.put("success", false);
            obj.put("message", "not a valid number");
            return obj;
        }
        Database d = new Database();
        try
        {
            obj = d.sell( symbol, quantity, username );
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        d.close();
        return obj;
    }
    
}