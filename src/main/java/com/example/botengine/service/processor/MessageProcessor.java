package com.example.botengine.service.processor;

import com.example.botengine.entity.SymphonyMessage;

public interface MessageProcessor {
    void process(SymphonyMessage message);
}
