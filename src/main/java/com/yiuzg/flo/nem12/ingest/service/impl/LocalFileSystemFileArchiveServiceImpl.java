package com.yiuzg.flo.nem12.ingest.service.impl;

import com.yiuzg.flo.nem12.ingest.dto.FileIngestDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.FileSystemFileIngestDto;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "flo.nem12.ingest.archive.target", havingValue = "file-system-local")
public class LocalFileSystemFileArchiveServiceImpl implements FileArchiveService
{
    private final String archiveLocation;

    @Autowired
    public LocalFileSystemFileArchiveServiceImpl(
            @Value("${flo.nem12.ingest.archive.${flo.nem12.ingest.archive.target}.location}") String archiveLocation)
    {
        this.archiveLocation = archiveLocation;
    }

    @Override
    public Mono<FileIngestDto> archive(FilePart file) throws IOException
    {
        Path archive = Files.createFile(Path.of(archiveLocation, UUID.randomUUID().toString()));
        return file.transferTo(archive)
                .thenReturn(new FileSystemFileIngestDto(
                        file.filename(),
                        archive.toAbsolutePath().toString()
                ));
    }
}
