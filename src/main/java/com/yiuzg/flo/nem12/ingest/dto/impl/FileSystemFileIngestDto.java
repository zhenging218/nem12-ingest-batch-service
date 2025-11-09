package com.yiuzg.flo.nem12.ingest.dto.impl;

import com.yiuzg.flo.nem12.ingest.dto.FileIngestDto;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FileSystemFileIngestDto implements FileIngestDto
{
    private String filename;
    private String objectKey;
    private Map<String, String> attributes;

    public FileSystemFileIngestDto(String filename, String objectKey) {
        this.filename = filename;
        this.objectKey = objectKey;
        this.attributes = new HashMap<>();
    }
}
