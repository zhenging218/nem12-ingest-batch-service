package com.yiuzg.flo.nem12.ingest.service;

import java.io.File;
import java.io.IOException;

public interface FileArchiveService
{
    void archive(File file) throws IOException;
}
