package com.yiuzg.flo.nem12.ingest.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Slf4j
@UtilityClass
public class BatchJobUtil
{
    public static <T extends Serializable> String createJobParameterString(ObjectMapper objectMapper, T value) {
        try
        {
            return objectMapper.writeValueAsString(value);
        }
        catch (JsonProcessingException e)
        {
            return null;
        }
    }

    public static <T extends Serializable> T parseJobParameterString(ObjectMapper objectMapper, String value, Class<T> valueType) {
        try
        {
            return StringUtils.isEmpty(value) ? null : objectMapper.readValue(value, valueType);
        }
        catch (JsonProcessingException e)
        {
            log.warn("Cannot parse value {} as type {}", value, valueType.getName());
            return null;
        }
    }
}
