package it.unimib.devtrinity.moneymind.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {

   public static Long bigDecimalToLong(BigDecimal value) {
      return value == null ? null : value.multiply(BigDecimal.valueOf(100)).longValue();
   }

   public static BigDecimal longToBigDecimal(Long value) {
      return value == null ? null : BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
   }

}
