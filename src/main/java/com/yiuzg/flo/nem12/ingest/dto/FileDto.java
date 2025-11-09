package com.yiuzg.flo.nem12.ingest.dto;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FileDto implements Serializable
{
    private String filename;
    private String objectKey;
    private Map<String, String> attributes;

    public FileDto(String filename, String objectKey) {
        this.filename = filename;
        this.objectKey = objectKey;
        this.attributes = new HashMap<>();
    }
}
