package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
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

    public static void showEndDatePicker(OnDateSelectedListener listener, Fragment fragment, TextInputEditText startDateField) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateString = startDateField.getText().toString();
        Date startDate;
        try {
            startDate = dateFormat.parse(startDateString);
        } catch (ParseException e) {
            Log.e("Utils", "Error parsing start date", e);
            return;
        }
        if (startDate == null) {
            return;
        }
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.from(startDate.getTime()));
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleziona una data")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(selection));
            listener.onDateSelected(formattedDate);
        });
        datePicker.show(fragment.getParentFragmentManager(), "DATE_PICKER");
    }
    public static void showNonFutureDatePicker(OnDateSelectedListener listener, Fragment fragment) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleziona una data")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(selection));
            listener.onDateSelected(formattedDate);
        });
        datePicker.show(fragment.getParentFragmentManager(), "DATE_PICKER");
    }

    public static List<String> getCurrencyDropdownItems(List<String> currencyCodes) {
        List<String> items = new ArrayList<>();

        for (String currencyCode : currencyCodes) {
            items.add(CurrencyHelper.getCurrencyDescription(currencyCode));
        }

        return items;
    }

    public static void makeSnackBar(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
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

    public static boolean isDataOutdated(Date latestStoredDate) {
        TimeZone cetTimeZone = TimeZone.getTimeZone("Europe/Paris");
        Calendar calendar = Calendar.getInstance(cetTimeZone);
        calendar.setTimeZone(cetTimeZone);
        calendar.setTime(latestStoredDate);

        int safeCycle = 0;
        while (safeCycle < 20 && (HolidayHelper.isWeekend(calendar) || HolidayHelper.isBceHoliday(calendar))) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            safeCycle++;
        }

        if(safeCycle >= 20){
            Log.e("ExchangeRepository", "Errore: impossibile trovare un giorno lavorativo valido dopo 20 tentativi.");
            return true;
        }

        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long lastPossibleUpdate = calendar.getTimeInMillis();

        Calendar now = Calendar.getInstance(cetTimeZone);
        now.setTimeZone(cetTimeZone);

        return now.getTimeInMillis() > lastPossibleUpdate;
    }

}