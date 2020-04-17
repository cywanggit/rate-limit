package com.example.demo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName MongoConfig
 * @create 2020-04-11 13:40
 */
@Configuration
@ConfigurationProperties(prefix = "mongo")
public class MongoConfig {

    private String address;

    private String dbName;

    /**
     * The number of connections allowed per host
     */
    private Integer connections;

    private Integer socketTimeout;

    private Integer waitTime;

    private Integer connectionTimeout;
    /**
     * 可阻塞线程倍数。例如如果 blockConnectMultiple=5  connections = 10 则可阻塞线程为 5*10=50。如果超过50 则抛出异常
     */
    private Integer blockConnectMultiple;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getConnections() {
        return connections;
    }

    public void setConnections(Integer connections) {
        this.connections = connections;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getBlockConnectMultiple() {
        return blockConnectMultiple;
    }

    public void setBlockConnectMultiple(Integer blockConnectMultiple) {
        this.blockConnectMultiple = blockConnectMultiple;
    }

    @Bean
    public MongoClient mongoClient(){
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.maxWaitTime(waitTime).connectionsPerHost(connections).connectTimeout(connectionTimeout).threadsAllowedToBlockForConnectionMultiplier(blockConnectMultiple)
                .socketTimeout(socketTimeout);
        MongoClientOptions options = builder.build();
        MongoClient mongoClient = new MongoClient(address,options);
        return mongoClient;

    }

    @Bean
    public MongoMappingContext mongoMappingContext(){
        return new MongoMappingContext();
    }

    @Bean
    public DefaultMongoTypeMapper defaultMongoTypeMapper(){
        return new DefaultMongoTypeMapper(null);
    }
}
