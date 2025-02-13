package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.ui.OnDateSelectedListener;

public class Utils {

    private static final String FORMAT_UI = "dd/MM/yyyy";
    private static final String FORMAT_API = "yyyy-MM-dd";

    public static Long bigDecimalToLong(BigDecimal value) {
        return value == null ? null : value.multiply(BigDecimal.valueOf(100)).longValue();
    }

    public static BigDecimal longToBigDecimal(Long value) {
        return value == null ? null : BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
    }

    private static Date parseDate(String dateString, String format) {
        try {
            return new SimpleDateFormat(format, Locale.getDefault()).parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    private static String formatDate(Date date, String format) {
        return (date == null) ? null : new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    public static Date stringToDate(String dateString) {
        return parseDate(dateString, FORMAT_UI);
    }

    public static String dateToString(Date date) {
        return formatDate(date, FORMAT_UI);
    }

    public static Date stringToDateApi(String dateString) {
        return parseDate(dateString, FORMAT_API);
    }

    public static String dateToStringApi(Date date) {
        return formatDate(date, FORMAT_API);
    }

    public static BigDecimal safeParseBigDecimal(String input, BigDecimal defaultValue) {
        if (TextUtils.isEmpty(input)) {
            return defaultValue;
        }

        try {
            return new BigDecimal(input);
        } catch (NumberFormatException e) {
            Log.e("InputError", "Invalid number format: " + input, e);
            return defaultValue;
        }
    }

    public static void showDatePicker(OnDateSelectedListener listener, Fragment fragment) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleziona una data")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(selection));
            listener.onDateSelected(formattedDate);
        });

        datePicker.show(fragment.getParentFragmentManager(), "DATE_PICKER");
    }

    public static void showEndDatePicker(OnDateSelectedListener listener, Fragment fragment, Date startDate, String title) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        if (startDate != null) {
            constraintsBuilder.setValidator(DateValidatorPointForward.from(startDate.getTime()));
        }

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(title)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat(FORMAT_UI, Locale.getDefault())
                    .format(new Date(selection));
            listener.onDateSelected(formattedDate);
        });

        datePicker.show(fragment.getParentFragmentManager(), "DATE_PICKER");
    }

    public static void showEndDatePicker(OnDateSelectedListener listener, Fragment fragment, TextInputEditText startDateField, String title) {
        showEndDatePicker(listener, fragment, stringToDate(startDateField.getText().toString()), title);
    }

    public static void showNonFutureDatePicker(OnDateSelectedListener listener, Fragment fragment, String title) {
        showEndDatePicker(listener, fragment, (Date) null, title);
    }

    public static List<String> getCurrencyDropdownItems(List<String> currencyCodes, Context context) {
        List<String> items = new ArrayList<>();

        for (String currencyCode : currencyCodes) {
            items.add(CurrencyHelper.getCurrencyDescription(context, currencyCode));
        }

        return items;
    }

    public static void makeSnackBar(View view, String message) {
        try {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        } catch (Exception ignored){

        }
    }

    public static String formatTransactionAmount(BigDecimal amount) {
        return String.format(Locale.getDefault(), "%.2f €", amount);
    }

    public static String formatTransactionAmount(BigDecimal amount, MovementTypeEnum movementTypeEnum) {
        return String.format(Locale.getDefault(), "%s %.2f €", movementTypeEnum == MovementTypeEnum.INCOME ? "+" : "-", amount);
    }

    public static String formatConvertedAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN).toPlainString();
    }

    public static String getMonthFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return String.format("%02d%d", month, year);
    }

    public static String formatMonthYear(String monthYear) {
        int month = Integer.parseInt(monthYear.substring(0, 2)) - 1;
        int year = Integer.parseInt(monthYear.substring(2, 6));

        String monthName = new DateFormatSymbols(Locale.getDefault()).getMonths()[month];
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);

        return monthName + " " + year;
    }

    public static long getStartDateFromMonthsBack(int monthsBack) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -monthsBack);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static int getMonthsDifference(long oldestDate) {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.DAY_OF_MONTH, 1);

        Calendar oldest = Calendar.getInstance();
        oldest.setTimeInMillis(oldestDate);
        oldest.set(Calendar.DAY_OF_MONTH, 1);

        int diffYear = current.get(Calendar.YEAR) - oldest.get(Calendar.YEAR);
        int diffMonth = current.get(Calendar.MONTH) - oldest.get(Calendar.MONTH);

        return diffYear * 12 + diffMonth;
    }

    public static Date getDateNDaysAgo(Date date, int daysAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo);
        return calendar.getTime();
    }

    public static Date getDataBceValid(Date date) {
        TimeZone cetTimeZone = TimeZone.getTimeZone("Europe/Paris");
        Calendar calendar = Calendar.getInstance(cetTimeZone);
        calendar.setTimeZone(cetTimeZone);
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 16) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        int safeCycle = 0;
        while (safeCycle < 20 && (HolidayHelper.isWeekend(calendar) || HolidayHelper.isBceHoliday(calendar))) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            safeCycle++;
        }

        if (safeCycle >= 20) {
            Log.e("ExchangeRepository", "Errore: impossibile trovare un giorno lavorativo valido dopo 20 tentativi.");
            return date;
        }

        return calendar.getTime();
    }

    public static void closeKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    public static List<Date> getAllMissingGenerationDates(RecurringTransactionEntity rt) {
        List<Date> missingDates = new ArrayList<>();

        Date now = new Date();
        Date lastGenerated = rt.getLastGeneratedDate();
        Date startDate = rt.getDate();
        Date endDate = rt.getRecurrenceEndDate();

        Calendar nextGenerationDate = Calendar.getInstance();

        if (lastGenerated == null) {
            nextGenerationDate.setTime(startDate);
        } else {
            nextGenerationDate.setTime(lastGenerated);
        }

        while (true) {
            Date nextDate = getNextGenerationDate(nextGenerationDate.getTime(), rt.getRecurrenceType(), rt.getRecurrenceInterval());

            if (!nextDate.before(now)) break;
            if (endDate != null && nextDate.after(endDate)) break;

            missingDates.add(nextDate);
            nextGenerationDate.setTime(nextDate);
        }

        return missingDates;
    }

    public static boolean shouldGenerateTransaction(RecurringTransactionEntity rt) {
        Date now = new Date();
        Date lastGenerated = rt.getLastGeneratedDate();

        if (lastGenerated == null) return true;

        Date nextGeneration = getNextGenerationDate(lastGenerated, rt.getRecurrenceType(), rt.getRecurrenceInterval());

        boolean shouldGenerate = !nextGeneration.after(now);
        if (rt.getRecurrenceEndDate() != null && nextGeneration.after(rt.getRecurrenceEndDate())) {
            return false;
        }

        return shouldGenerate;
    }

    private static Date getNextGenerationDate(Date baseDate, RecurrenceTypeEnum type, int interval) {
        Calendar nextDate = Calendar.getInstance();
        nextDate.setTime(baseDate);

        switch (type) {
            case DAILY:
                nextDate.add(Calendar.DAY_OF_YEAR, interval);
                break;
            case WEEKLY:
                nextDate.add(Calendar.WEEK_OF_YEAR, interval);
                break;
            case MONTHLY:
                nextDate.add(Calendar.MONTH, interval);
                break;
            case YEARLY:
                nextDate.add(Calendar.YEAR, interval);
                break;
            default:
                throw new IllegalArgumentException("Tipo di ricorrenza non valido: " + type);
        }

        return nextDate.getTime();
    }

}