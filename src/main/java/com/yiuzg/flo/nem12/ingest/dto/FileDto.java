package com.yiuzg.flo.nem12.ingest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FileDto
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
