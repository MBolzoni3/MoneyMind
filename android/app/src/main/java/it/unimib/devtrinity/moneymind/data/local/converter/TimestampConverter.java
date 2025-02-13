package it.unimib.devtrinity.moneymind.data.local.converter;

import androidx.room.TypeConverter;

import com.google.firebase.Timestamp;

import java.util.Date;

public class TimestampConverter {

    @TypeConverter
    public static Date fromLong(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Timestamp fromLongToTimestamp(Long value) {
        return value == null ? null : new Timestamp(new Date(value));
    }

    @TypeConverter
    public static Long toLongFromTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toDate().getTime();
    }
}
