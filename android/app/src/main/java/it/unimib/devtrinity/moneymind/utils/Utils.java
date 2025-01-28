package it.unimib.devtrinity.moneymind.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.material.appbar.MaterialToolbar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;

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
                return R.drawable.ic_trending_up;
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

}
