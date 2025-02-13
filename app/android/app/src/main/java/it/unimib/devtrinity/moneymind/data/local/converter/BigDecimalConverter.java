package it.unimib.devtrinity.moneymind.data.local.converter;

import androidx.room.TypeConverter;

import java.math.BigDecimal;

import it.unimib.devtrinity.moneymind.utils.Utils;

public class BigDecimalConverter {

    @TypeConverter
    public static Long bigDecimalToLong(BigDecimal value) {
        return Utils.bigDecimalToLong(value);
    }

    @TypeConverter
    public static BigDecimal longToBigDecimal(Long value) {
        return Utils.longToBigDecimal(value);
    }

}
