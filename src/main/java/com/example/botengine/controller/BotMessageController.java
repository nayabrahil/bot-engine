package com.example.botengine.controller;

import com.example.botengine.entity.SymphonyMessage;
import com.example.botengine.repo.SymphonyMsgRepository;
import com.example.botengine.service.processor.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class BotMessageController {

    public static final Logger logger = LoggerFactory.getLogger(BotMessageController.class);

    private MessageProcessor messageProcessor;
    private SymphonyMsgRepository symphonyMsgRepository;

    @Autowired
    public BotMessageController(MessageProcessor messageProcessor, SymphonyMsgRepository symphonyMsgRepository) {
        this.messageProcessor = messageProcessor;
        this.symphonyMsgRepository = symphonyMsgRepository;
    }

    @Bean
    RouterFunction<ServerResponse> composedRoutes() {
        return
                RouterFunctions.route(
                        GET("/symphonyMessages"),
                        req -> ServerResponse.ok().body(
                                symphonyMsgRepository.findAll(), SymphonyMessage.class))

                        .and(route(GET("/messages/{id}"),
                                req -> ok().body(
                                        symphonyMsgRepository.findById(req.pathVariable("id")), SymphonyMessage.class)))

                        .and(route(
                                POST("/message"),
                                req -> req.body(BodyExtractors.toMono(SymphonyMessage.class))
                                        .doOnNext(messageProcessor::process)
                                        .then(ok().build())));
    }


}
