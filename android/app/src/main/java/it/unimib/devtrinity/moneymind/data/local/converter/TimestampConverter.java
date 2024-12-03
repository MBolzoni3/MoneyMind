package it.unimib.devtrinity.moneymind.data.local.converter;

import androidx.room.TypeConverter;

import com.google.firebase.Timestamp;

import java.util.Date;

public class TimestampConverter {

    // Conversione Room: Long <-> Date
    @TypeConverter
    public static Date fromLong(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date date) {
        return date == null ? null : date.getTime();
    }

    // Conversione Firestore: Long <-> Firebase Timestamp
    @TypeConverter
    public static Timestamp fromLongToTimestamp(Long value) {
        return value == null ? null : new Timestamp(new Date(value));
    }

    @TypeConverter
    public static Long toLongFromTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toDate().getTime();
    }
}
