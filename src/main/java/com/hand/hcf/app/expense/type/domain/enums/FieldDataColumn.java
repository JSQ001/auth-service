package com.hand.hcf.app.expense.type.domain.enums;

import com.hand.hcf.core.enums.SysEnum;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by markfredchen on 16/1/10.
 */
public enum FieldDataColumn implements SysEnum {
    LONG_COL_1(101, "LongCol1", Long.class),
    LONG_COL_2(102, "LongCol2", Long.class),
    LONG_COL_3(103, "LongCol3", Long.class),
    LONG_COL_4(104, "LongCol4", Long.class),
    LONG_COL_5(105, "LongCol5", Long.class),
    STRING_COL_1(111, "StringCol1", String.class),
    STRING_COL_2(112, "StringCol2", String.class),
    STRING_COL_3(113, "StringCol3", String.class),
    STRING_COL_4(114, "StringCol4", String.class),
    STRING_COL_5(115, "StringCol5", String.class),
    STRING_COL_6(116, "StringCol6", String.class),
    STRING_COL_7(117, "StringCol7", String.class),
    STRING_COL_8(118, "StringCol8", String.class),
    STRING_COL_9(119, "StringCol9", String.class),
    STRING_COL_10(120, "StringCol10", String.class),
    DATE_TIME_COL_1(121, "DateTimeCol1", ZonedDateTime.class),
    DATE_TIME_COL_2(122, "DateTimeCol2", ZonedDateTime.class),
    DATE_TIME_COL_3(123, "DateTimeCol3", ZonedDateTime.class),
    DATE_TIME_COL_4(124, "DateTimeCol4", ZonedDateTime.class),
    DATE_TIME_COL_5(125, "DateTimeCol5", ZonedDateTime.class),
    DOUBLE_COL_1(131, "DoubleCol1", Double.class),
    DOUBLE_COL_2(132, "DoubleCol2", Double.class),
    DOUBLE_COL_3(133, "DoubleCol3", Double.class),
    DOUBLE_COL_4(134, "DoubleCol4", Double.class),
    DOUBLE_COL_5(135, "DoubleCol5", Double.class),
    DATE_COL_1(141, "DateCol1", ZonedDateTime.class),
    DATE_COL_2(142, "DateCol2", ZonedDateTime.class),
    DATE_COL_3(143, "DateCol3", ZonedDateTime.class),
    DATE_COL_4(144, "DateCol4", ZonedDateTime.class),
    DATE_COL_5(145, "DateCol5", ZonedDateTime.class),
    LOCATION_COL_1(151, "LocationCol1", String.class),
    LOCATION_COL_2(152, "LocationCol2", String.class),
    PARTICIPANT_COL_1(161, "ParticipantCol1", String.class),
    PARTICIPANT_COL_2(161, "ParticipantCol2", String.class),
    PARTICIPANTS_COL_1(171, "ParticipantsCol1", String.class),
    /**
     * 固定字段：订单金额
     */
    ORDER_AMOUNT(200, "OrderAmount", BigDecimal.class),
    /**
     * 固定字段：订单币种
     */
    ORDER_CURRENCY(201, "OrderCurrency", String.class);

    private Integer id;
    private String columnName;
    private Class type;

    FieldDataColumn(Integer id, String columnName, Class type) {
        this.id = id;
        this.columnName = columnName;
        this.type = type;
    }

    public static FieldDataColumn parse(Integer id) {
        for (FieldDataColumn fieldDataColumn : FieldDataColumn.values()) {
            if (fieldDataColumn.getId().equals(id)) {
                return fieldDataColumn;
            }
        }
        return null;
    }

    @Deprecated
    public static Map<FieldType, List<FieldDataColumn>> getAllInvoiceDataColumn() {
        Map<FieldType, List<FieldDataColumn>> result = new HashMap<>();
        result.put(FieldType.LONG, Arrays.asList(LONG_COL_1, LONG_COL_2, LONG_COL_3, LONG_COL_4, LONG_COL_5));
        result.put(FieldType.TEXT, Arrays.asList(STRING_COL_1, STRING_COL_2, STRING_COL_3, STRING_COL_4, STRING_COL_5, STRING_COL_6, STRING_COL_7, STRING_COL_8, STRING_COL_9, STRING_COL_10));
        result.put(FieldType.DATETIME, Arrays.asList(DATE_TIME_COL_1, DATE_TIME_COL_2, DATE_TIME_COL_3, DATE_TIME_COL_4, DATE_TIME_COL_5));
        result.put(FieldType.DOUBLE, Arrays.asList(DOUBLE_COL_1, DOUBLE_COL_2, DOUBLE_COL_3, DOUBLE_COL_4, DOUBLE_COL_5));
        result.put(FieldType.DATE, Arrays.asList(DATE_COL_1, DATE_COL_2, DATE_COL_3, DATE_COL_4, DATE_COL_5));
        result.put(FieldType.GPS, Arrays.asList(STRING_COL_1, STRING_COL_2, STRING_COL_3, STRING_COL_4, STRING_COL_5, STRING_COL_6, STRING_COL_7, STRING_COL_8, STRING_COL_9, STRING_COL_10));
        result.put(FieldType.LOCATION, Arrays.asList(LOCATION_COL_1, LOCATION_COL_2));
        result.put(FieldType.PARTICIPANT, Arrays.asList(PARTICIPANT_COL_1, PARTICIPANT_COL_2));
        result.put(FieldType.PARTICIPANTS, Arrays.asList(PARTICIPANTS_COL_1));
        result.put(FieldType.MONTH, Arrays.asList(DATE_COL_1, DATE_COL_2, DATE_COL_3, DATE_COL_4, DATE_COL_5));
        result.put(FieldType.POSITIVE_INTEGER, Arrays.asList(LONG_COL_1, LONG_COL_2, LONG_COL_3, LONG_COL_4, LONG_COL_5));
        return result;
    }

    public static List<FieldDataColumn> getAllFieldDataColumn() {
        return Arrays.asList(STRING_COL_1, STRING_COL_2, STRING_COL_3, STRING_COL_4, STRING_COL_5, STRING_COL_6, STRING_COL_7, STRING_COL_8, STRING_COL_9, STRING_COL_10);
    }

    /**
     * 不同类型的字段所有的可用映射map
     * @return
     */
    public static Map<FieldDataTypeEnum, List<FieldDataColumn>> getFieldDataColumnMap() {
        Map<FieldDataTypeEnum, List<FieldDataColumn>> result = new HashMap<>();
        //字符
        result.put(FieldDataTypeEnum.TEXT,new ArrayList<>(Arrays.asList(STRING_COL_1, STRING_COL_2, STRING_COL_3, STRING_COL_4,
            STRING_COL_5, STRING_COL_6, STRING_COL_7, STRING_COL_8, STRING_COL_9, STRING_COL_10,
            LOCATION_COL_1, LOCATION_COL_2,PARTICIPANT_COL_1,PARTICIPANT_COL_2)));
        //日期
        result.put(FieldDataTypeEnum.DATE,new ArrayList<>(Arrays.asList(DATE_COL_1, DATE_COL_2, DATE_COL_3, DATE_COL_4, DATE_COL_5,DATE_TIME_COL_1, DATE_TIME_COL_2, DATE_TIME_COL_3, DATE_TIME_COL_4, DATE_TIME_COL_5)));
        //整型
        result.put(FieldDataTypeEnum.LONG,new ArrayList<>(Arrays.asList(LONG_COL_1, LONG_COL_2, LONG_COL_3, LONG_COL_4, LONG_COL_5)));
        //浮点
        result.put(FieldDataTypeEnum.DOUBLE,new ArrayList<>(Arrays.asList(DOUBLE_COL_1, DOUBLE_COL_2, DOUBLE_COL_3, DOUBLE_COL_4, DOUBLE_COL_5)));
        return result;
    }


    @Override
    public Integer getId() {
        return this.id;
    }

    public String getColumnName() {
        return columnName;
    }

}
