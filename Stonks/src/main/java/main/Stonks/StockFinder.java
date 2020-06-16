package main.Stonks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class StockFinder
{
    private Crawler[] crawlers;
    
    public StockFinder()
    {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions o = new ChromeOptions();
        o.addArguments( "--headless" );
        o.addArguments( "--ignore-certificate-errors" );
        o.addArguments( "detach=true" );
        
        ArrayList<Crawler> crawlerList= new ArrayList<Crawler>();
        while(true)
        {
            try
            {
                WebDriver driver = new ChromeDriver(o);
                Crawler crawler = new Crawler(driver);
                crawlerList.add( crawler );
                crawler.start();
            }
            catch(NoClassDefFoundError e)
            {
                break;
            }
        }
        
        crawlers = new Crawler[crawlerList.size()];
        for (int i = 0; i < crawlerList.size(); i++)
        {
            crawlers[i] = crawlerList.get( i );
        }
    }
    
    public HashMap<String, Double> find(String[] strings)
    {
        ManageHub hub = new ManageHub(crawlers, strings);
        while(!hub.isComplete())
        {
            try
            {
                Thread.sleep( 100 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
        HashMap<String, Double> map = hub.getResults();
        hub.close();
        return map;
    }
    
    public HashMap<String, Double> find(String string)
    {
        String[] strings = new String[] {string};
        ManageHub hub = new ManageHub(crawlers, strings);
        while(!hub.isComplete())
        {
            try
            {
                Thread.sleep( 100 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
        HashMap<String, Double> map = hub.getResults();
        hub.close();
        return map;
    }
    
    public void close()
    {
        for (Crawler c : crawlers)
        {
            c.close();
        }
    }
    
    public boolean allClosed()
    {
        boolean done = true;
        for (Crawler c : crawlers)
        {
            done = done && (!c.isAlive());
            if(!done)
            {
                return done;
            }
        }
        return done;
    }
}
