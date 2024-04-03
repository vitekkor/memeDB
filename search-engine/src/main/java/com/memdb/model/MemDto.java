package com.memdb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemDto {
    private String id;
    private String type;

    public MemDto(Mem mem) {
        this(mem.getUuid(), mem.getType());
    }
}
