package it.unimib.devtrinity.moneymind.data.local.converter;

import androidx.room.TypeConverter;

import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;

public class MovementTypeConverter {

    @TypeConverter
    public static String movementTypeToString(MovementTypeEnum value) {
        return value == null ? null : value.name();
    }

    @TypeConverter
    public static MovementTypeEnum stringToMovementType(String value) {
        return value == null ? null : MovementTypeEnum.valueOf(value);
    }

}
