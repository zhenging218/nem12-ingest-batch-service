package com.yiuzg.flo.nem12.ingest.service;

import com.yiuzg.flo.nem12.ingest.dto.FileIngestDto;
import reactor.core.publisher.Mono;

public interface Nem12IngestService
{
    // todo: return a meaningful response dto regarding the ingest job
    Mono<String> ingest(FileIngestDto fileIngest);
}
