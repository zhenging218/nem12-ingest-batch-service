package com.yiuzg.flo.nem12.ingest.controller;

import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.service.Nem12IngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@ConditionalOnExpression("!'${spring.main.web-application-type}'.equals('none')")
@RestController("app/nem12/ingest")
public class IngestController
{
    private final Nem12IngestService nem12IngestService;
    private final FileArchiveService fileArchiveService;

    @Autowired
    public IngestController(Nem12IngestService nem12IngestService, FileArchiveService fileArchiveService)
    {
        this.nem12IngestService = nem12IngestService;
        this.fileArchiveService = fileArchiveService;
    }

    @PostMapping(path = "file")
    Mono<ResponseEntity<String>> postIngestFile(@RequestPart("file") FilePart file) throws IOException
    {
        return fileArchiveService.archive(file)
                .map(nem12IngestService::ingest)
                .map(jobId -> ResponseEntity.accepted().body(jobId))
                .onErrorResume(error ->
                        Mono.just(ResponseEntity.internalServerError().body(error.getMessage())));
    }
}
