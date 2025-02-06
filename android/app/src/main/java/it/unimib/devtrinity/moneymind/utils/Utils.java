package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;

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

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.ui.OnDateSelectedListener;

public class Utils {

    public static Long bigDecimalToLong(BigDecimal value) {
        return value == null ? null : value.multiply(BigDecimal.valueOf(100)).longValue();
    }

    public static BigDecimal longToBigDecimal(Long value) {
        return value == null ? null : BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
    }

    public static Date stringToDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static int getThemeColor(Context context, int colorAttribute) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttribute, typedValue, true);
        return typedValue.data;
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

    public static int getCategoryIcon(CategoryEntity categoryEntity) {
        if (categoryEntity == null || categoryEntity.getName() == null) {
            return R.drawable.ic_money_bag;
        }

        String categoryName = categoryEntity.getName().toLowerCase();
        switch (categoryName) {
            case "lavoro":
                return R.drawable.ic_work;
            case "investimenti":
                return R.drawable.ic_finance_mode;
            case "casa":
                return R.drawable.ic_home;
            case "utilità":
                return R.drawable.ic_build;
            case "trasporti":
                return R.drawable.ic_directions_car;
            case "alimentazione":
                return R.drawable.ic_restaurant;
            case "salute e benessere":
                return R.drawable.ic_favorite;
            case "educazione":
                return R.drawable.ic_school;
            case "svago":
                return R.drawable.ic_sports_esports;
            case "varie":
                return R.drawable.ic_category;
            default:
                return R.drawable.ic_money_bag;
        }
    }

    public static int getTypeIcon(MovementTypeEnum movementTypeEnum) {
        return movementTypeEnum == MovementTypeEnum.INCOME ? R.drawable.ic_trending_up : R.drawable.ic_trending_down;
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

    public static List<String> getCurrencyDropdownItems(List<String> currencyCodes) {
        List<String> items = new ArrayList<>();

        for (String currencyCode : currencyCodes) {
            items.add(CurrencyHelper.getCurrencyDescription(currencyCode));
        }

        return items;
    }

    public static String formatTransactionAmount(BigDecimal amount) {
        return String.format(Locale.getDefault(), "%.2f €", amount);
    }

    public static String formatTransactionAmount(BigDecimal amount, MovementTypeEnum movementTypeEnum) {
        return String.format(Locale.getDefault(), "%s %.2f €", movementTypeEnum == MovementTypeEnum.INCOME ? "+" : "-", amount);
    }

    public static String getMonthFromDate(Date date){
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


}
