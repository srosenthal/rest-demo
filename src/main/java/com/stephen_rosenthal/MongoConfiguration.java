package com.stephen_rosenthal;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * Configuration for the connection to MongoDB.
 */
@Configuration
public class MongoConfiguration {

    @Bean
    public MongoDbFactory mongoDbFactory(
            @Value("${mongoHostName}") String hostName,
            @Value("${mongoPort}") int port,
            @Value("${mongoDatabaseName}") String databaseName) throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(hostName, port), databaseName);
    }
}
