package it.unimib.devtrinity.moneymind.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

   public static Long bigDecimalToLong(BigDecimal value) {
      return value == null ? null : value.multiply(BigDecimal.valueOf(100)).longValue();
   }

   public static BigDecimal longToBigDecimal(Long value) {
      return value == null ? null : BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
   }

   public static Date stringToDate(String dateString) {
      try {
         SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
         return dateFormat.parse(dateString);
      } catch (ParseException e) {
         return null;
      }
   }

}
