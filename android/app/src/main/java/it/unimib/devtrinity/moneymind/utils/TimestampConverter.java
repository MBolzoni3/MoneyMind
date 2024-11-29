package it.unimib.devtrinity.moneymind.utils;

import androidx.room.TypeConverter;
import com.google.firebase.Timestamp;

import java.util.Date;

public class TimestampConverter {

   @TypeConverter
   public static Timestamp fromLong(Long value) {
      return value == null ? null : new Timestamp(new Date(value));
   }

   @TypeConverter
   public static Long toLong(Timestamp timestamp) {
      return timestamp == null ? null : timestamp.toDate().getTime();
   }
}
