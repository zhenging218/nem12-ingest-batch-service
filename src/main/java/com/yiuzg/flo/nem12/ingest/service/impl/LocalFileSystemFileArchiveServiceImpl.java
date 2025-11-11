package com.yiuzg.flo.nem12.ingest.service.impl;

import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "flo.nem12.ingest.archive.target", havingValue = "file-system-local")
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
    public Mono<FileDto> archive(FilePart file) throws IOException
    {
        Path archive = Files.createFile(
                Path.of(archiveLocation, UUID.randomUUID().toString()));
        return file.transferTo(archive)
                .thenReturn(new FileDto(file.filename(),
                        archive.toAbsolutePath().toString()
                ));
    }

    @Override
    public InputStream retrieve(FileDto file) throws IOException
    {
        return Files.newInputStream(Paths.get(file.getObjectKey()));
    }
}
