package com.memdb.service.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptionCallbackDto {
    private String id;
    private String description;
    private StatusType status;
    private String error;
}
