package it.unimib.devtrinity.moneymind.data.local.converter;

import androidx.room.TypeConverter;

import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;

public class RecurrenceTypeConverter {

    @TypeConverter
    public static String recurrenceTypeToString(RecurrenceTypeEnum value) {
        return value == null ? null : value.name();
    }

    @TypeConverter
    public static RecurrenceTypeEnum stringToRecurrenceType(String value) {
        return value == null ? null : RecurrenceTypeEnum.valueOf(value);
    }

}
