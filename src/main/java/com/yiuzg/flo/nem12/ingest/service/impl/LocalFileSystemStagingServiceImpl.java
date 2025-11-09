package com.yiuzg.flo.nem12.ingest.service.impl;

import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.service.FileStagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.function.Predicate;

@ConditionalOnProperty(name = "flo.nem12.ingest.staging.target", havingValue = "file-system-local")
@Service
public class LocalFileSystemStagingServiceImpl implements FileStagingService
{
    private final String location;
    private final int bufferSize;

    @Autowired
    public LocalFileSystemStagingServiceImpl(
            @Value("${flo.nem12.ingest.staging.file-system-local.location}") String location,
            @Value("${flo.nem12.ingest.staging.file-system-local.buffer-size:1024}") int bufferSize)
    {
        this.location = location;
        this.bufferSize = bufferSize;
    }

    @Override
    public FileDto stage(String filename, InputStream inputStream, Predicate<Integer> receivePredicate) throws IOException
    {
        Path stagedPath = Path.of(location, UUID.randomUUID().toString()).toAbsolutePath();

        try (OutputStream outputStream = Files.newOutputStream(stagedPath, StandardOpenOption.CREATE))
        {
            byte[] buf = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buf, 0, buf.length)) > 0)
            {
                outputStream.write(buf, 0, len);
            }
        }

        return new FileDto(filename, stagedPath.toString());
    }

    @Override
    public FileDto stage(String filename, InputStream contents) throws IOException
    {
        return stage(filename, contents, len -> !(len < 0));
    }

    @Override
    public InputStream retrieve(FileDto file) throws IOException
    {
        return Files.newInputStream(Paths.get(file.getObjectKey()));
    }
}
