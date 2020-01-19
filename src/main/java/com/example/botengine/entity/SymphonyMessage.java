package com.example.botengine.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class SymphonyMessage {
    @Id
    private String id;
    private String streamId;
    private String message;
}