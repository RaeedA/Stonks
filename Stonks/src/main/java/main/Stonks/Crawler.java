package main.Stonks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Crawler extends Thread
{
    private boolean active = true;
    private boolean busy = false;
    private ArrayList<String> symbols;
    private HashMap<String,Double> results;
    private WebDriver driver;
    
    public Crawler(WebDriver drive)
    {
        driver = drive;
    }
    
    @Override
    public void run()
    {
        while(active)
        {
            if(symbols.isEmpty())
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
            else
            {
                getInfo();
            }
        }
        driver.close();
        driver.quit();
    }
    
    public void getInfo()
    {
        busy = true;
        String current;
        while(!symbols.isEmpty())
        {
            current = symbols.get( 0 );
            symbols.remove( 0 );
            driver.get( "https://finance.yahoo.com/quote/" + current );
            try
            {
                WebElement element = driver.findElement( By.cssSelector( "Trsdu(0.3s).Fw(b)" ) );
                results.put( current.toUpperCase(), Double.parseDouble( element.getText() ) );
            }
            catch(NoSuchElementException e)
            {
                continue;
            }
        }
        busy = false;
    }
    
    public boolean addJob(ArrayList<String> symbolList, HashMap<String,Double> resultMap)
    {
        if(busy)
        {
            return false;
        }
        else
        {
            results = resultMap;
            symbols = symbolList;
            return true;
        }
    }
    
    public void close()
    {
        active = false;
    }
}
