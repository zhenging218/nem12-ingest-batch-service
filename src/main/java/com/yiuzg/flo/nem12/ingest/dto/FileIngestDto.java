package com.yiuzg.flo.nem12.ingest.dto;

import java.util.Map;

public interface FileIngestDto
{
    String getFilename();
    String getObjectKey();
    Map<String, String> getAttributes();
}
