package com.example.botengine.service.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;

@Service
public class NLP_Parser implements MessageParser{

    private Queue queue;

    @Autowired
    public NLP_Parser(@Qualifier("symphonyMsgQueue") Queue queue){
        this.queue=queue;
    }

    @Override
    public List<String> parse() {
        return null;
    }
}
