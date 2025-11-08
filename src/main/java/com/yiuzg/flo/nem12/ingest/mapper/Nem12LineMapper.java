package com.yiuzg.flo.nem12.ingest.mapper;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class Nem12LineMapper implements LineMapper<Nem12RecordDto>
{
    private final DelimitedLineTokenizer tokenizer;

    public Nem12LineMapper(DelimitedLineTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private String[] tokenizeToRecord(String line) {
        var fieldSet = tokenizer.tokenize(line);

    }

    @Override
    public Nem12RecordDto mapLine(String line, int lineNumber) throws Exception
    {
        // tokenize and interpret record type of line only
        // processor will determine processing logic
        // validate each record for record type value validate only
        return null;
    }
}
