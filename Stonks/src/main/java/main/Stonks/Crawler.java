package main.Stonks;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Crawler extends Thread
{
    private boolean active = true;
    private boolean busy = false;
    private String mySymbol;
    private double myPrice;
    private WebDriver driver;
    private ManageHub myHub;
    
    public Crawler(WebDriver drive)
    {
        myHub = null;
        mySymbol = "";
        myPrice = 0.0;
        driver = drive;
    }
    
    @Override
    public void run()
    {
        while(active)
        {
            if(myHub == null)
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
                while(myHub.hasJobs())
                {
                    if (getJob())
                    {
                        doJob();
                        returnJob();
                    }
                }
            }
        }
        driver.close();
        driver.quit();
    }
    
    private void doJob()
    {
        driver.get( "https://finance.yahoo.com/quote/" + mySymbol );
        List<WebElement> elements = driver.findElements( By.xpath( "/html/body/div[1]/div/div/div[1]/div/div[2]/div/div/div[4]/div/div/div/div[3]/div/div/span[1]" ) );
        if(elements.size() == 0)
        {
        }
        else
        {
            myPrice = Double.parseDouble( elements.get( 0 ).getText());
        }
        busy = false;
    }
    
    //Gets a task from hub
    public void giveTask(String symbol)
    {
        mySymbol = symbol;
    }
    
    //Returns job to hub and resets variables
    private void returnJob()
    {
        myHub.recieveTask( mySymbol, myPrice );
        mySymbol = null;
        myPrice = 0.0;
    }
    
    //Gets a job from hub
    private boolean getJob()
    {
        busy = true;
        boolean worked = myHub.assignTask(this);
        if(worked)
        {
            return worked;
        }
        else
        {
            busy = false;
            return worked;
        }
    }
    
    //Closes this thread
    public void close()
    {
        active = false;
    }
    
    //Deletes the hub
    public void deleteHub()
    {
        myHub = null;
    }
    
    //Adds new hub
    public void assignHub(ManageHub hub)
    {
        myHub = hub;
    }
}
