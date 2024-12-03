package it.unimib.devtrinity.moneymind.data.local.converter;

import androidx.room.TypeConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalConverter {

    @TypeConverter
    public static Long bigDecimalToLong(BigDecimal value) {
        return value == null ? null : value.multiply(BigDecimal.valueOf(100)).longValue();
    }

    @TypeConverter
    public static BigDecimal longToBigDecimal(Long value) {
        return value == null ? null : BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

}
