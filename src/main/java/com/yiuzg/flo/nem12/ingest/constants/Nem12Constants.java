package com.yiuzg.flo.nem12.ingest.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Nem12Constants
{
    public static final String NEM12_START_IND = "0";
    public static final String NEM12_HEADER_IND = "100";
    public static final String NEM12_NMI_DETAIL_IND = "200";
    public static final String NEM12_INTERVAL_DATA_IND = "300";
    public static final String NEM12_INTERVAL_EVENT_IND = "400";
    public static final String NEM12_B2B_DETAILS_IND = "500";
    public static final String NEM12_END_IND = "900";

    public static final int NEM12_INTERVAL_COUNT_DIVIDEND = 1440;

    // Date(8)
    public static final String DT_REVERSE = "yyyyMMdd";
    // DateTime(12)
    public static final String DT_HM_REVERSE = "yyyyMMddHHmm";
    // DateTime(14)
    public static final String DT_HMS_REVERSE = "yyyyMMddHHmmss";
}
