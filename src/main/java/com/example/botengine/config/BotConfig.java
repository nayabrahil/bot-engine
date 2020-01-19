package com.example.botengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BotConfig {

    @Bean("symphonyMsgQueue")
    public Queue<String> getConcurrentLinkedQueue(){
        return new ConcurrentLinkedQueue<>();
    }

    @Bean("readExecutor")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(2);
    }
}
