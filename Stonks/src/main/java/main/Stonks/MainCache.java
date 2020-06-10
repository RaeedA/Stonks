package main.Stonks;

import java.util.concurrent.TimeUnit;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

public class MainCache
{
    @SuppressWarnings("deprecation")
    public static void main(String[] args)
    {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        CacheConfigurationBuilder<Long, String> builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10));
        builder.withExpiry( Expirations.timeToLiveExpiration( new Duration(30L, TimeUnit.MINUTES) ) );
        CacheConfiguration<Long, String> config = builder.build();
        Cache<Long, String> myCache = cacheManager.createCache("myCache",config);
        cacheManager.close();
    }
}
