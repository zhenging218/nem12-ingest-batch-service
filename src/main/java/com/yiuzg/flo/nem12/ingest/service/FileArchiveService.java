package com.yiuzg.flo.nem12.ingest.service;

import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

public interface FileArchiveService
{
    Mono<FileDto> archive(FilePart file) throws IOException;

    InputStream retrieve(FileDto file) throws IOException;
}
