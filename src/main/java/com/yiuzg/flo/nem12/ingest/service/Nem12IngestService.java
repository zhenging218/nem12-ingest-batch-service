package com.yiuzg.flo.nem12.ingest.service;

import com.yiuzg.flo.nem12.ingest.dto.FileDto;

public interface Nem12IngestService
{
    // todo: return a meaningful response dto regarding the ingest job
    String ingest(FileDto fileIngest);
}
