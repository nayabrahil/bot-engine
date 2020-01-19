package com.example.botengine.service.processor;

import com.example.botengine.entity.SymphonyMessage;
import com.example.botengine.repo.SymphonyMsgRepository;
import com.example.botengine.service.reader.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SymphonyMessageProcessor implements MessageProcessor {

    private Reader reader;
    private SymphonyMsgRepository symphonyMsgRepository;

    @Autowired
    public SymphonyMessageProcessor(Reader reader, SymphonyMsgRepository symphonyMsgRepository){
        this.reader = reader;
        this.symphonyMsgRepository = symphonyMsgRepository;
    }

    @Override
    public void process(SymphonyMessage message) {
        symphonyMsgRepository.save(message);
        reader.read(message.getMessage());
    }
}
