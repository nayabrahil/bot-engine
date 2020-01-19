package com.example.botengine.service.reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

@Service
public class InboundMessageReader implements Reader {

    private ExecutorService executorService;
    private Queue<String> queue;
    private Consumer<String> messageConsumer = s -> queue.add(s);

    @Autowired
    public InboundMessageReader(@Qualifier("symphonyMsgQueue") Queue queue,
                                @Qualifier("readExecutor") ExecutorService executorService) {
        this.queue = queue;
        this.executorService = executorService;
    }

    @Override
    public void read(String message) {
        executorService.submit(() -> messageConsumer.accept(message));
    }
}
