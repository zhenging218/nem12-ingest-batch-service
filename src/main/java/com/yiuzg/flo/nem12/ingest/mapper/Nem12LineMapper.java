package com.yiuzg.flo.nem12.ingest.mapper;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class Nem12LineMapper implements LineMapper<Nem12RecordDto>
{
    private final DelimitedLineTokenizer tokenizer;
    // current read 200 header (nmi identified)
    // to be set to null when completing the current 200 range (i.e. 900 reached)
    private String nmi;

    public Nem12LineMapper(DelimitedLineTokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.nmi = null;
    }

    private String[] tokenizeToRecord(String line) {
        var fieldSet = tokenizer.tokenize(line);

    }

    @Override
    public Nem12RecordDto mapLine(String line, int lineNumber) throws Exception
    {

        if(nmi == null) {
            // read 200
        } else {

        }
        return null;
    }
}
