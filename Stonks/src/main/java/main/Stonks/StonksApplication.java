package main.Stonks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.json.simple.JSONObject;

@SpringBootApplication
@RestController
@SuppressWarnings("unchecked")
public class StonksApplication
{
    static CacheManager manager;
    static Cache cache;
    static StockFinder finder;
    
    public static void main(String[] args)
    {
        finder = new StockFinder();
        manager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        makeCache();
        SpringApplication.run(StonksApplication.class, args);
    }
    
    private static void makeCache()
    {
        CacheConfigurationBuilder<Long, String> builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10));
        builder.withExpiry( Expirations.timeToLiveExpiration( new Duration(30L, TimeUnit.MINUTES) ) );
        cache = manager.createCache("myCache",builder.build());
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name)
    {
        
        return String.format("Hello %s!", name);
    }
    
    @GetMapping("/exit")
    public void exit()
    {
        manager.close();
        System.exit( 0 );
    }
    
    @GetMapping("/test")
    public String test()
    {
        Database d = new Database(finder);
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
        Database d = new Database(finder);
        try
        {
            obj = d.buy( symbol, quantity, username );
        }
        catch ( SQLException e )
        {
            obj.put( "success", false );
            obj.put( "message", e.getMessage());
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
        Database d = new Database(finder);
        try
        {
            obj = d.sell( symbol, quantity, username );
        }
        catch ( SQLException e )
        {
            obj.put( "success", false );
            obj.put( "message", e.getMessage());
        }
        d.close();
        return obj;
    }
    
    @GetMapping("/accountstatus")
    public JSONObject accstatus(@RequestParam(value = "username", defaultValue = "") String username)
    {
        JSONObject obj = new JSONObject();
        
        if (username.equals( "" ))
        {
            obj.put("success", false);
            obj.put( "message", "not enough information");
            return obj;
        }
        Database d = new Database(finder);
        try
        {
            obj = d.accstatus(username);
        }
        catch(SQLException e)
        {
            obj.put("success", false);
            obj.put( "message", e.getMessage());
        }
        d.close();
        return obj;
    }
    
}