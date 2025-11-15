package com.yiuzg.flo.nem12.ingest.mapper;

import com.yiuzg.flo.nem12.ingest.constants.Nem12Constants;
import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.*;
import com.yiuzg.flo.nem12.ingest.utilities.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.math.BigDecimal;
import java.util.stream.IntStream;

public class Nem12FieldSetMapper implements FieldSetMapper<Nem12RecordDto>
{
    private String currentNmi;
    private int expectedValues;
    private boolean endRecordReached;

    private String getRecordIdentifier(String[] values) {
        return values[0];
    }

    private Nem12HeaderRecordDto read100Header(String[] values) {
        return new Nem12HeaderRecordDto(getRecordIdentifier(values));
    }

    private Nem12NMIDetailRecordDto read200Record(String[] values) {
        Nem12NMIDetailRecordDto result = new Nem12NMIDetailRecordDto();
        result.setRecordIndicator(getRecordIdentifier(values));
        result.setNmi(values[1]);
        currentNmi = result.getNmi();
        result.setIntervalLength(Integer.parseInt(values[8]));
        expectedValues = Nem12Constants.NEM12_INTERVAL_COUNT_DIVIDEND / result.getIntervalLength();
        return result;
    }

    private Nem12IntervalDataRecordDto read300Record(String[] values) {
        if(StringUtils.isBlank(currentNmi)) {
            throw new IllegalArgumentException("Unexpected 300 record before a 200 record was read");
        }

        if(values.length != expectedValues + 7) {
            throw new IllegalArgumentException(
                    String.format("Unexpected amount of interval values in a 300 record (expected %d, got %d)",
                            expectedValues, values.length - 7));
        }

        Nem12IntervalDataRecordDto result = new Nem12IntervalDataRecordDto();
        result.setNmi(currentNmi);
        result.setRecordIndicator(getRecordIdentifier(values));
        result.setIntervalDate(DateUtil.stringToLocalDate(values[1], Nem12Constants.DT_REVERSE));
        result.setIntervalValues(IntStream.range(0, expectedValues)
                .mapToObj(i -> new BigDecimal(values[i + 2])).toList());

        result.setQualityMethod(values[expectedValues + 2]);
        if(StringUtils.isNotBlank(values[expectedValues + 2 + 1])) {
            result.setReasonCode(Integer.parseInt(values[expectedValues + 2 + 1]));
        }
        result.setReasonDescription(values[expectedValues + 2 + 2]);

        if(StringUtils.isNotBlank(values[expectedValues + 2 + 3])) {
            result.setUpdateDateTime(DateUtil.stringToLocalDateTime(values[expectedValues + 2 + 3],
                    Nem12Constants.DT_HMS_REVERSE));
        }

        if(StringUtils.isNotBlank(values[expectedValues + 2 + 4]))
        {
            result.setMsatsLoadDateTime(DateUtil.stringToLocalDateTime(values[expectedValues + 2 + 4],
                    Nem12Constants.DT_HMS_REVERSE));
        }

        return result;
    }

    private Nem12IntervalEventRecordDto read400Record(String[] values) {
        if(StringUtils.isBlank(currentNmi)) {
            throw new IllegalArgumentException("Unexpected 400 record before a 200 record was read");
        }
        return new Nem12IntervalEventRecordDto(getRecordIdentifier(values));
    }

    private Nem12B2BDetailsRecordDto read500Record(String[] values) {
        if(StringUtils.isBlank(currentNmi)) {
            throw new IllegalArgumentException("Unexpected 500 record before a 200 record was read");
        }
        return new Nem12B2BDetailsRecordDto(getRecordIdentifier(values));
    }

    private Nem12EndRecordDto read900Record(String[] values) {
        endRecordReached = true;
        return new Nem12EndRecordDto(getRecordIdentifier(values));
    }

    public Nem12FieldSetMapper()
    {
        this.expectedValues = 0;
        this.currentNmi = null;
        this.endRecordReached = false;
    }

    @Override
    public Nem12RecordDto mapFieldSet(FieldSet fieldSet) throws BindException
    {
        String[] values = fieldSet.getValues();

        if(!endRecordReached)
        {
            return switch (values[0])
            {
                case Nem12Constants.NEM12_HEADER_IND -> read100Header(values);
                case Nem12Constants.NEM12_NMI_DETAIL_IND -> read200Record(values);
                case Nem12Constants.NEM12_INTERVAL_DATA_IND -> read300Record(values);
                case Nem12Constants.NEM12_INTERVAL_EVENT_IND -> read400Record(values);
                case Nem12Constants.NEM12_B2B_DETAILS_IND -> read500Record(values);
                case Nem12Constants.NEM12_END_IND -> read900Record(values);
                default ->
                        throw new IllegalArgumentException(String.format("unable to parse record identifier %s", values[0]));
            };
        } else {
            throw new IllegalStateException("Parsed end indicator, but expectedly able to read more records after it");
        }
    }
}
