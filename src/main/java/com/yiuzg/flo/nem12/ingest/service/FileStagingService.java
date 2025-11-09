package com.yiuzg.flo.nem12.ingest.service;

import com.yiuzg.flo.nem12.ingest.dto.FileDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

public interface FileStagingService
{
    FileDto stage(String filename, InputStream inputStream, Predicate<Integer> receivePredicate) throws IOException;

    FileDto stage(String filename, InputStream contents) throws IOException;

    InputStream retrieve(FileDto file) throws IOException;
}
