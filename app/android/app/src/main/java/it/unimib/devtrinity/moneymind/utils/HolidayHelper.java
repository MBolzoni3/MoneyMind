package it.unimib.devtrinity.moneymind.utils;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class HolidayHelper {

    private static final Set<String> BCE_HOLIDAYS = Set.of(
            "01-01", //Capodanno
            "01-05", //Festa del Lavoro
            "03-10", //Giornata dell'Unità Tedesca
            "24-12", //Vigilia di Natale
            "25-12", //Natale
            "26-12", //Santo Stefano
            "31-12"  //San Silvestro
    );

    private static final Map<Integer, Set<String>> HOLIDAY_CACHE = new ConcurrentHashMap<>();

    private static Set<String> getBceHolidays(int year) {
        return HOLIDAY_CACHE.computeIfAbsent(year, y -> {
            Set<String> holidays = new HashSet<>(BCE_HOLIDAYS);

            Calendar easterSunday = getEasterSunday(year);

            Calendar goodFriday = (Calendar) easterSunday.clone();
            goodFriday.add(Calendar.DAY_OF_MONTH, -2);
            holidays.add(formatDate(goodFriday));

            Calendar easterMonday = (Calendar) easterSunday.clone();
            easterMonday.add(Calendar.DAY_OF_MONTH, 1);
            holidays.add(formatDate(easterMonday));

            Calendar ascension = (Calendar) easterSunday.clone();
            ascension.add(Calendar.DAY_OF_MONTH, 39);
            holidays.add(formatDate(ascension));

            Calendar pentecostMonday = (Calendar) easterSunday.clone();
            pentecostMonday.add(Calendar.DAY_OF_MONTH, 50);
            holidays.add(formatDate(pentecostMonday));

            Calendar corpusChristi = (Calendar) easterSunday.clone();
            corpusChristi.add(Calendar.DAY_OF_MONTH, 60);
            holidays.add(formatDate(corpusChristi));

            return Collections.unmodifiableSet(holidays);
        });
    }

    public static boolean isWeekend(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    public static boolean isBceHoliday(Calendar calendar) {
        return getBceHolidays(calendar.get(Calendar.YEAR)).contains(formatDate(calendar));
    }

    private static Calendar getEasterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;

        TimeZone cetTimeZone = TimeZone.getTimeZone("Europe/Paris");
        Calendar easter = Calendar.getInstance(cetTimeZone);
        easter.setTimeZone(cetTimeZone);
        easter.clear();
        easter.set(year, month - 1, day);

        return easter;
    }

    private static String formatDate(Calendar calendar) {
        return String.format("%02d-%02d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1);
    }
}
