package com.yiuzg.flo.nem12.ingest.service.impl;

import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class LocalFileSystemFileArchiveServiceImpl implements FileArchiveService
{
    private final String archiveLocation;

    @Autowired
    public LocalFileSystemFileArchiveServiceImpl(
            @Value("${flo.nem12.ingest.archive.file-system-local.location}") String archiveLocation,
            @Value("${flo.nem12.ingest.archive.file-system-local.location.auto-create}") boolean autoCreateDirectory)
            throws IOException
    {
        this.archiveLocation = archiveLocation;

        if(autoCreateDirectory) {
            Files.createDirectories(Path.of(archiveLocation).toAbsolutePath());
        }
    }

    @Override
    public void archive(File file) throws IOException
    {
        Files.move(file.toPath().toAbsolutePath(), Path.of(archiveLocation, file.getName()),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING);
    }
}
