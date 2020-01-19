package com.example.botengine.repo;

import com.example.botengine.entity.SymphonyMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface SymphonyMsgRepository extends ReactiveMongoRepository<SymphonyMessage, String> {
}
