package main.Stonks;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class ManageHub
{
    private int completed;
    private int totalTasks;
    private Crawler[] crawlers;
    private HashMap<String, Double> results;
    private Stack<String> symbols;
    
    //Constructor
    public ManageHub(Crawler[] crawlerList, String[] symbolList)
    {
        //Gets stack of symbols ready
        symbols = new Stack<String>();
        for (String s : symbolList)
        {
            symbols.push( s );
        }
        
        totalTasks = symbolList.length;
        completed = 0;
        
        results = new HashMap<String, Double>(totalTasks);
        
        crawlers = crawlerList;
        for(Crawler c : crawlers)
        {
            c.assignHub( this );
        }
    }
    
    //Gives task to crawler
    public synchronized boolean assignTask(Crawler crawler)
    {
        try
        {
            crawler.giveTask( symbols.pop() );
            return true;
        }
        catch(EmptyStackException e)
        {
            return false;
        }
    }
    
    //Gets task from crawler
    public synchronized void recieveTask(String symbol, double price)
    {
        if(price == 0.0)
        {
        }
        else
        {
            results.put( symbol, price );
        }
        completed++;
    }
    
    //Checks if it has a job
    public synchronized boolean hasJobs()
    {
        return !symbols.isEmpty();
    }
    
    //Checks if it is done
    public boolean isComplete()
    {
        return completed == totalTasks;
    }
    
    //Returns final results
    public HashMap<String, Double> getResults()
    {
        return results;
    }
    
    public void close()
    {
        for(Crawler c : crawlers)
        {
            c.deleteHub();
        }
    }
}
