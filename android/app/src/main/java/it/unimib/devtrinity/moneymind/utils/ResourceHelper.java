package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;
import android.util.TypedValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;

public class ResourceHelper {

    private static final Map<String, Integer> CATEGORY_MAP = Map.of(
            "lavoro", R.string.category_lavoro,
            "investimenti", R.string.category_investimenti,
            "casa", R.string.category_casa,
            "utilità", R.string.category_utilita,
            "trasporti", R.string.category_trasporti,
            "alimentazione", R.string.category_alimentazione,
            "salute e benessere", R.string.category_salute_benessere,
            "educazione", R.string.category_educazione,
            "svago", R.string.category_svago,
            "varie", R.string.category_varie
    );

    public static int getThemeColor(Context context, int colorAttribute) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttribute, typedValue, true);
        return typedValue.data;
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

    public static String getCategoryName(Context context, String categoryKey) {
        Integer resId = CATEGORY_MAP.get(categoryKey.toLowerCase());
        return (resId != null) ? context.getString(resId) : categoryKey;
    }

    public static int getTypeIcon(MovementTypeEnum movementTypeEnum) {
        return movementTypeEnum == MovementTypeEnum.INCOME ? R.drawable.ic_trending_up : R.drawable.ic_trending_down;
    }

    public static String getBudgetMessage(Context context, BigDecimal spent, BigDecimal budget) {
        if (spent == null) spent = BigDecimal.ZERO;
        if (budget == null) budget = BigDecimal.ZERO;

        String spentFormatted = Utils.formatTransactionAmount(spent);
        String budgetFormatted = Utils.formatTransactionAmount(budget);

        if (spent.compareTo(BigDecimal.ZERO) == 0) {
            return context.getString(R.string.budget_not_used, budgetFormatted);
        } else if (spent.compareTo(budget.multiply(new BigDecimal("0.75"))) < 0) {
            return context.getString(R.string.budget_under_control, spentFormatted, budgetFormatted);
        } else if (spent.compareTo(budget) < 0) {
            return context.getString(R.string.budget_warning, spentFormatted, budgetFormatted);
        } else {
            return context.getString(R.string.budget_exceeded, spentFormatted, budgetFormatted);
        }
    }

    public static String getGoalMessage(Context context, BigDecimal saved, BigDecimal goal) {
        if (saved == null) saved = BigDecimal.ZERO;
        if (goal == null) goal = BigDecimal.ZERO;

        String savedFormatted = Utils.formatTransactionAmount(saved);
        String goalFormatted = Utils.formatTransactionAmount(goal);

        if (saved.compareTo(BigDecimal.ZERO) == 0) {
            return context.getString(R.string.savings_no_progress, goalFormatted);
        } else if (saved.compareTo(goal.multiply(new BigDecimal("0.75"))) < 0) {
            return context.getString(R.string.savings_in_progress, savedFormatted, goalFormatted);
        } else if (saved.compareTo(goal) < 0) {
            return context.getString(R.string.savings_almost_there, savedFormatted, goalFormatted);
        } else {
            return context.getString(R.string.savings_goal_reached, savedFormatted, goalFormatted);
        }
    }

    public static String getFormattedDateRange(Context context, Date startDate, Date endDate) {
        String start = String.format(Locale.getDefault(), "%1$td/%1$tm/%1$ty", startDate);
        String end = String.format(Locale.getDefault(), "%1$td/%1$tm/%1$ty", endDate);
        return context.getString(R.string.date_range, start, end);
    }

    public static String getCurrentBalanceMessage(Context context, BigDecimal incomes, BigDecimal expenses) {
        if (incomes == null) incomes = BigDecimal.ZERO;
        if (expenses == null) expenses = BigDecimal.ZERO;

        BigDecimal balance = incomes.subtract(expenses);

        if (expenses.compareTo(BigDecimal.ZERO) == 0 && incomes.compareTo(BigDecimal.ZERO) == 0) {
            return context.getString(R.string.balance_no_activity);
        }

        if (expenses.compareTo(BigDecimal.ZERO) == 0) {
            return context.getString(R.string.balance_excellent);
        }

        if (incomes.compareTo(BigDecimal.ZERO) == 0 && expenses.compareTo(BigDecimal.ZERO) > 0) {
            return context.getString(R.string.balance_critical);
        }

        BigDecimal deficitPercentage = balance.divide(expenses, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            return context.getString(R.string.balance_good);
        } else if (deficitPercentage.compareTo(new BigDecimal("-10")) > 0) {
            return context.getString(R.string.balance_neutral);
        } else if (deficitPercentage.compareTo(new BigDecimal("-50")) > 0) {
            return context.getString(R.string.balance_slightly_negative);
        } else {
            return context.getString(R.string.balance_critical);
        }
    }

    public static String getWelcomeMessage(Context context, String username) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return context.getString(R.string.welcome_morning, username);
        } else if (hour >= 12 && hour < 18) {
            return context.getString(R.string.welcome_afternoon, username);
        } else {
            return context.getString(R.string.welcome_evening, username);
        }
    }

}
