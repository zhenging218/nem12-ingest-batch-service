package com.yiuzg.flo.nem12.ingest.constants;

import lombok.Getter;
import org.apache.commons.lang3.Strings;

import java.util.Arrays;

@Getter
public enum EStagingState
{
    NEW("N"),
    FAILED("F"),
    ERROR("E"),
    SUCCESS("Y");

    private final String code;

    EStagingState(String code)
    {
        this.code = code;
    }

    public static EStagingState fromCode(String code) {
        return Arrays.stream(values())
                .filter(value -> Strings.CS.equals(value.getCode(), code))
                .findFirst().orElse(null);
    }
}
