package it.unimib.devtrinity.moneymind.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.google.android.material.appbar.MaterialToolbar;

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

    public static void setTopAppBarColor(Activity activity, MaterialToolbar topAppBar, int backgroundColorAttr, int textColorAttr) {
        Context context = topAppBar.getContext();
        int backgroundColor = getThemeColor(context, backgroundColorAttr);
        int textColor = getThemeColor(context, textColorAttr);

        topAppBar.setBackgroundColor(backgroundColor);
        topAppBar.setTitleTextColor(textColor);
        topAppBar.setNavigationIconTint(textColor);

        for (int i = 0; i < topAppBar.getMenu().size(); i++) {
            Drawable icon = topAppBar.getMenu().getItem(i).getIcon();
            if (icon != null) {
                icon.setTint(textColor);
            }
        }

        syncStatusBarWithTopAppBar(activity, topAppBar);
    }

    public static void syncStatusBarWithTopAppBar(Activity activity, MaterialToolbar topAppBar) {
        Drawable background = topAppBar.getBackground();
        if (background instanceof ColorDrawable) {
            int color = ((ColorDrawable) background).getColor();
            activity.getWindow().setStatusBarColor(color);
        }
    }

}
