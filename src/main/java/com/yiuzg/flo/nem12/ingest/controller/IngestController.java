package com.yiuzg.flo.nem12.ingest.controller;

import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.service.Nem12IngestService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController("/app/nem12/ingest")
public class IngestController
{
    private final Nem12IngestService nem12IngestService;
    private final FileArchiveService fileArchiveService;

    public IngestController(Nem12IngestService nem12IngestService, FileArchiveService fileArchiveService)
    {
        this.nem12IngestService = nem12IngestService;
        this.fileArchiveService = fileArchiveService;
    }

    @PostMapping(path = {"/file", "/file/"})
    Mono<ResponseEntity<String>> postIngestFile(@RequestPart("file") FilePart file) throws IOException
    {
        return fileArchiveService.archive(file)
                .flatMap(nem12IngestService::ingest)
                .map(jobId -> ResponseEntity.accepted().body(jobId));
    }
}
