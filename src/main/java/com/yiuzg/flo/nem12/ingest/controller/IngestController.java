package com.yiuzg.flo.nem12.ingest.controller;

import com.yiuzg.flo.nem12.ingest.dto.IngestResponseDto;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.service.Nem12IngestService;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping(path = {"/app/nem12/ingest", "/app/nem12/ingest/"})
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

    @PostMapping(path = {"/file","/file/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Mono<IngestResponseDto>> postIngestFile(@RequestPart("file") FilePart file) throws IOException
    {
        return ResponseEntity.accepted().body(fileArchiveService.archive(file)
                .map(nem12IngestService::ingest));
    }
}
